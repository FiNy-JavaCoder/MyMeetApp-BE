package MyApp.BE.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

public enum Regions {
        STREDOCESKY_KRAJ("Středočeský kraj",
                        Districts.BENESOV, Districts.BEROUN, Districts.KLADNO, Districts.KOLIN,
                        Districts.KUTNA_HORA, Districts.MELNIK, Districts.MLADA_BOLESLAV,
                        Districts.NYMBURK, Districts.PRAHA_VYCHOD, Districts.PRAHA_ZAPAD,
                        Districts.PRIBRAM, Districts.RAKOVNIK),
        JIHOCESKY_KRAJ("Jihočeský kraj",
                        Districts.CESKE_BUDEJOVICE, Districts.CESKY_KRUMLOV, Districts.JINDRICHUV_HRADEC,
                        Districts.PISEK, Districts.PRACHATICE, Districts.STRAKONICE, Districts.TABOR),
        PLZENSKY_KRAJ("Plzeňský kraj",
                        Districts.DOMAZLICE, Districts.KLATOVY, Districts.PLZEN_MESTO, Districts.PLZEN_JIH,
                        Districts.PLZEN_SEVER, Districts.ROKYCANY, Districts.TACHOV),
        KARLOVARSKY_KRAJ("Karlovarský kraj",
                        Districts.CHEB, Districts.KARLOVY_VARY, Districts.SOKOLOV),
        USTECKY_KRAJ("Ústecký kraj",
                        Districts.CHOMUTOV, Districts.DECIN, Districts.LITOMERICE, Districts.LOUNY,
                        Districts.MOST, Districts.TEPLICE, Districts.USTI_NAD_LABEM),
        LIBERECKY_KRAJ("Liberecký kraj",
                        Districts.CESKA_LIPA, Districts.JABLONEC_NAD_NISOU, Districts.LIBEREC, Districts.SEMILY),
        KRALOVEHRADECKY_KRAJ("Královéhradecký kraj",
                        Districts.HRADEC_KRALOVE, Districts.JICIN, Districts.NACHOD, Districts.RYCHNOV_NAD_KNEZNOU,
                        Districts.TRUTNOV),
        PARDUBICKY_KRAJ("Pardubický kraj",
                        Districts.CHRUDIM, Districts.PARDUBICE, Districts.SVITAVY, Districts.USTI_NAD_ORLICI),
        KRAJ_VYSOCINA("Kraj Vysočina",
                        Districts.HAVLICKUV_BROD, Districts.JIHLAVA, Districts.PELHRIMOV,
                        Districts.TREBIC, Districts.ZDAR_NAD_SAZAVOU),
        JIHOMORAVSKY_KRAJ("Jihomoravský kraj",
                        Districts.BLANSKO, Districts.BRNO_MESTO, Districts.BRNO_VENKOV, Districts.BRECLAV,
                        Districts.HODONIN, Districts.VYSKOV, Districts.ZNOJMO),
        OLOMOUCKY_KRAJ("Olomoucký kraj",
                        Districts.JESENIK, Districts.OLOMOUC, Districts.PROSTEJOV,
                        Districts.PREROV, Districts.SUMPERK),
        MORAVSKOSLEZSKY_KRAJ("Moravskoslezský kraj",
                        Districts.BRUNTAL, Districts.FRYDEK_MISTEK, Districts.KARVINA, Districts.NOVY_JICIN,
                        Districts.OPAVA, Districts.OSTRAVA_MESTO),
        ZLINSKY_KRAJ("Zlínský kraj",
                        Districts.KROMERIZ, Districts.UHERSKE_HRADISTE, Districts.VSETIN, Districts.ZLIN);

        private final String name;
        private final Set<Districts> districts;

        Regions(String name, Districts... districts) {
                this.name = name;
                this.districts = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(districts)));
        }

        public String getName() {
                return name;
        }

        public Set<Districts> getDistricts() {
                return districts;
        }
}