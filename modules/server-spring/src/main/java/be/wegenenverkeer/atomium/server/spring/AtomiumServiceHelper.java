package be.wegenenverkeer.atomium.server.spring;

import be.wegenenverkeer.atomium.japi.format.Content;
import be.wegenenverkeer.atomium.japi.format.Generator;
import be.wegenenverkeer.atomium.japi.format.Link;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;

/**
 * Zodat @Transactional correct wordt geproxied.
 */
@Component
public class AtomiumServiceHelper {

    @Value("${atomium.cacheDuration:2592000}") // default: cache for 30 days
    private int cacheDuration;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Update atom volgnummers (in aparte transactie).
     *
     * @param feedProvider feedProvider
     * @param <E> Entry waarop de feed wordt gebaseerd
     * @param <T> TO zoals deze in de feed moet verschijnen
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <E, T> void sync(FeedProvider<E, T> feedProvider) {
        feedProvider.sync();
    }

    /**
     * Eigenlijke bepalen van de atom feed voor wijzigingen in de entries.
     *
     * @param feedProvider entry provider
     * @param page pagina
     * @param request request
     * @param isCurrent wordt de feed opgevraagd langs "/"?
     * @param <E> Entry waarop de feed wordt gebaseerd
     * @param <T> TO zoals deze in de feed moet verschijnen
     * @return atom feed data
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <E, T> Response getFeed(FeedProvider<E, T> feedProvider, long page, Request request, boolean isCurrent) {
        // entries voor pagina bepalen (ééntje meer om te weten of er nog een volgende pagina is)
        List<E> entriesForPage = feedProvider.getEntriesForPage(page);

        if (entriesForPage.isEmpty()) {
            return Response.status(404).entity("Pagina " + page + " niet gevonden.").build();
        }

        boolean pageComplete = entriesForPage.size() > feedProvider.getPageSize();
        LocalDateTime updated = feedProvider.getTimestampForEntry(entriesForPage.get(0));

        // updated time known, check/calculate eTag
        CacheControl cc = new CacheControl();
        cc.setMaxAge(cacheDuration);
        Response.ResponseBuilder rb;
        EntityTag etag = new EntityTag(Integer.toString(updated.hashCode()));
        rb = request.evaluatePreconditions(etag); // Verify if it matched with etag available in http request
        if (null != rb) { // rb is not null when eTag matched
            return rb.cacheControl(cc).tag(etag).build();
        }

        // feed bouwen
        Feed<T> feed = new Feed<>(
                feedProvider.getFeedName(),
                feedProvider.getFeedUrl(),
                feedProvider.getFeedName(),
                new Generator(feedProvider.getProviderName(), feedProvider.getFeedUrl(), feedProvider.getProviderVersion()),
                updated
        );

        String pageUrl = "/" + page + '/' + feedProvider.getPageSize();

        List<AtomEntry<T>> entries = entriesForPage.stream()
                .map(entry -> toAtomEntry(entry, feedProvider))
                .collect(toList());

        feed.setEntries(entries);

        List<Link> links = new ArrayList<>();
        links.add(new Link("last", "/0/" + feedProvider.getPageSize()));
        if (page >= 1) {
            // volgens "REST in Practice" moet next en previous omgekeerd, voor atomium moet het in deze volgorde
            links.add(new Link("next", "/" + (page - 1) + '/' + feedProvider.getPageSize()));
        }
        if (pageComplete) {
            // volgens "REST in Practice" moet next en previous omgekeerd, voor atomium moet het in deze volgorde
            links.add(new Link("previous", "/" + (page + 1) + '/' + feedProvider.getPageSize()));

            // we hebben 1 element teveel opgehaald om een db call te besparen, maar die moet niet meegestuurd worden in deze page
            feed.getEntries().remove(0);
        }

        links.add(new Link("self", pageUrl));
        feed.setLinks(links);

        // response opbouwen
        try {
            rb = Response.ok(mapper.writeValueAsString(feed));
            if (!isCurrent && pageComplete) {
                rb.cacheControl(cc); // cache result enkel als pagina volledig en niet via de "recent" URL opgeroepen. Zie figuur 7-2 in Rest In Practice.
            }
            rb.tag(etag);
            return rb.build();
        } catch (JsonProcessingException jpe) {
            throw new AtomiumServerException("Kan stream niet converteren naar JSON.", jpe);
        }
    }

    /**
     * Entity to atom entry.
     *
     * @param entity entity
     * @param feedProvider feedProvider
     * @param <E> entity type
     * @param <T> to type
     * @return entity omgezet naar een TO
     */
    <E, T> AtomEntry<T> toAtomEntry(E entity, FeedProvider<E, T> feedProvider) {
        return new AtomEntry<T>(
                feedProvider.getUrnForEntry(entity),
                feedProvider.getTimestampForEntry(entity),
                new Content<>(feedProvider.toTo(entity), ""));
    }

}
