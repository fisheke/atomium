
import sbt._

object Dependencies {

  val playVersion = "2.4.2"
  val play25Version = "2.5.4"

  // main deps
  val logback           = "ch.qos.logback"              %     "logback-classic"               % "1.1.1"
  val jodaTime          = "joda-time"                   %     "joda-time"                     % "2.3"
  val jodaConvert       = "org.joda"                    %     "joda-convert"                  % "1.2"
  val rxscala           = "io.reactivex"                %%    "rxscala"                       % "0.24.0"
  val playJson          = "com.typesafe.play"           %%    "play-json"                     % playVersion
  val playWs            = "com.typesafe.play"           %%    "play-ws"                       % playVersion
  val play              = "com.typesafe.play"           %%    "play"                          % playVersion
  val mongoJavaDriver   = "org.mongodb"                 %     "mongo-java-driver"             % "2.0"
  val casbah            = "org.mongodb"                 %%    "casbah"                        % "2.8.0"
  val slick             = "com.typesafe.slick"          %%    "slick"                         % "2.1.0"
  val slickPostgres     = "com.github.tminglei"         %%    "slick-pg"                      % "0.7.0"
  val akkaPersistence   = "com.typesafe.akka"           %%    "akka-persistence-experimental" % "2.3.12"

  //play25 deps
  val play25Json          = "com.typesafe.play"           %%    "play-json"                     % play25Version
  val play25Ws            = "com.typesafe.play"           %%    "play-ws"                       % play25Version
  val play25              = "com.typesafe.play"           %%    "play"                          % play25Version
  val play25Test          = "com.typesafe.play"           %%    "play-test"                     % play25Version    % "test"

  // test deps
  val scalaTest         = "org.scalatest"               %%    "scalatest"               % "2.2.0"        % "test"
  val scalaTestPlay     = "org.scalatestplus"           %%    "play"                    % "1.2.0"        % "test"
  val scalaCheck        = "org.scalacheck"              %%    "scalacheck"              % "1.12.1"       % "test"
  val embededMongo      = "com.github.simplyscala"      %%    "scalatest-embedmongo"    % "0.2.2"        % "test"
  val h2database        = "com.h2database"              %     "h2"                      % "1.0.60"       % "test"
  val playMockWs        = "de.leanovate.play-mockws"    %%    "play-mockws"             % "0.12"         % "test"
  val playTest          = "com.typesafe.play"           %%    "play-test"               % playVersion    % "test"
  val wiremock          = "com.github.tomakehurst"      %     "wiremock"                % "1.57"         % "test"


  // java deps
  val junit             = "junit"                           %   "junit"                   % "4.11"    % "test"
  val junitInterface    = "com.novocode"                    %   "junit-interface"         % "0.11"    % "test"
  val jacksonDatabind   = "com.fasterxml.jackson.core"      %   "jackson-databind"        % "2.4.3"
  val jacksonJoda       = "com.fasterxml.jackson.datatype"  %   "jackson-datatype-joda"   % "2.4.3"
  val rxhttpclient      = "be.wegenenverkeer"               %   "rxhttpclient-java"       % "0.4.0"
  val lombok            = "org.projectlombok"               %   "lombok"                  % "1.16.10" % "provided"
  val springContext     = "org.springframework"             %   "spring-context"          % "4.2.0.RELEASE"
  val springTx          = "org.springframework"             %   "spring-tx"               % "4.2.0.RELEASE"
  val jaxRsApi          = "org.jboss.spec.javax.ws.rs"      %   "jboss-jaxrs-api_2.0_spec" % "1.0.0.Final"

  val slf4j             = "org.slf4j"                       %   "slf4j-api"               % "1.7.6"
  val mockitoCore       = "org.mockito"                     %   "mockito-core"            % "1.9.5"   % "test"
  val assertJ           = "org.assertj"                     %   "assertj-core"            % "1.5.0"   % "test"
  val commonsIo         = "commons-io"                      %   "commons-io"              % "2.4"     % "test"


  val mainDependencies = Seq(
    logback,
    jodaTime,
	  jodaConvert,
    junitInterface
  )

  val mainScalaTestDependencies = Seq (
    scalaTest,
    scalaCheck
  )


}