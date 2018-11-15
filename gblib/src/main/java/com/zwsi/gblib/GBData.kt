// Copyright 2018 Louis Perrochon. All rights reserved

// GBData contain
//  - All the global static data // TODO read data from a config file or other external data source
//    includes names, types and other attributes of stars, planets, sectors
//  - All the creation logic for stars, planets, sectors
//
// GB Data is a separate class because later we want to read this from an external source (JSON file, web, db, etc.)

package com.zwsi.gblib

import java.util.*

class GBData {

    init {} // Eventually we may be able to get rid of this, and be an object only

    companion object {

        // TODO these public methods be internal if the whole lib is Kotlin? Nobody outside the lib should call GBData
        // TODO once only Kotlin calls this code, do we still need to call it with ..Companion..

        private val rand = Random() // Our RNG. We could seed it for testing. Make it var, and assign in init block?

        private const val NumberOfStars = 25
        private const val UniverseMaxX = 1000
        private const val UniverseMaxY = 1000

        fun getNumberOfStars():Int { return NumberOfStars}
        fun getUniverseMaxX():Int { return UniverseMaxX}
        fun getUniverseMaxY():Int { return UniverseMaxY}

        fun selectStarNameIdx(): Int {
            // TODO no dupes in System Names - not a high priority, real world may have duplicates...
            return rand.nextInt(starNames.size)
        }

        fun starNameFromIdx(n: Int) : String {
            return starNames[n]
        }

        fun selectPlanetNameIdx(): Int {
            // TODO no dupes - not a high priority, real world may have duplicates...
            return rand.nextInt(planetNames.size)
        }

        fun planetNameFromIdx(n: Int) : String {
            return planetNames[n]
        }

        fun selectPlanetTypeIdx(): Int {
            // TODO specified distribution, instead of random
            return rand.nextInt(planetTypesNames.size)
        }

        fun planetTypeFromIdx(n: Int): String {
            return planetTypesNames[n]
        }

        // Get random, but universally distributed coordinates for stars
        // Approach: break up the universe into n areas of equal size, and put one star in each area
        // where n is the smallest square number bigger than numberOfStars. Then shuffle the areas
        private var areas: ArrayList<Int> = ArrayList() // we fill it up on first call to GetStarCoordinates

        fun getStarCoordinates() : IntArray {

            val nos = NumberOfStars.toDouble()
            val dim = java.lang.Math.ceil(java.lang.Math.sqrt(nos)).toInt()

            if (areas.isEmpty()) {
                for (i in 0 until dim * dim) {
                    areas.add(i, i)
                }
                areas.shuffle()
            }

            val coordinates = IntArray(size = 2)
            val area = areas.removeAt(0)

            val areaX = area % dim   // coordinates of the chosen area
            val areaY = area / dim   // coordinates of the chosen area
            val areaWidth = UniverseMaxX / dim
            val areaHeight = UniverseMaxY / dim
            val marginX = areaWidth / 10 // no star in the edge of the area
            val marginY = areaHeight / 10 // no star in the edge of the area

            coordinates[0] = rand.nextInt(areaWidth - 2*marginX) + areaX * areaWidth + marginX
            coordinates[1] = rand.nextInt(areaHeight - 2*marginY) + areaY * areaHeight + marginY

            return coordinates

        }

        // Get random, but type appropriate sectors
        // Coordinates are [down][right] with [0][0] top left.
        fun getSectors(planetType: Int): Array<Array<GBSector?>> {

            val height = rand.nextInt(planetTypesSize[planetType][1] - planetTypesSize[planetType][0]) +
                    planetTypesSize[planetType][0]
            val width = height * 2

            val sectors = Array (height) { arrayOfNulls<GBSector?> (width)  }

            for (h in 0 until height) {
                for (w in 0 until width) {
                    sectors[h][w] = GBSector()
                    sectors[h][w]?.type = sectorTypesChance[planetType][rand.nextInt(10)]
                    //sectors[h][w]?.type_symbol = sectorTypesConsole[sectors[h][w]?.type]
                    sectors[h][w]?.type_symbol = sectorTypesConsole[0]
                }
            }
            return sectors
        }

        // Stars
        // Stars from: https://github.com/kaladron/galactic-bloodshed/blob/master/data/star.list
        private val starNames = arrayOf(
            "Abel","Achernar","Acrab","Acticum","Adhara","Adara","Adonis","Akbar","Albireo","Alcor","Aldebran","Aleramin","Alferatz","Algol","Alhema","Alioth","Alkalurops","Almach","Alnath","Alpha","Altair","Aludra","Alya","Ananke","Andromeda","Antares","Aphrodite","Apocalypse","Aquarius","Ara","Archernar","Ares","Arioch","Artemis","Ascella","Auriga","Avior","Ayachi","Azagthoth","Baetica","Bear","Beholder","Bellatrix","Benetnasch","Betelgeuse","Bohr","Bones","Bootes","Boreas","Brahe","Brin","Brust","Bujold","Byron","Calypso","Camille","Caneb","Capella","Capricorn","Carina","Cassiopeia","Cat","Centauri","Cepheus","Cetus","Chi","Citurnae","Columba","Copernicus","Corona","Crater","Crux","Cthulhu","Cursa","Cygnus","Cylon","Dalos","Daphne","Dacia","Dark","Data","Death","Delphinus","Deneb","Dionysus","Diphda","Donaldson","Dorado","Draco","Dubhe","Ea","Eburacum","Edessa","Elrond","Elymais","Ekpe","Emporium","Eniph","Epirus","Epsilon","Eridanus","Erusa","Eshu","Eta","Fantor","Fermi","Fisher","Fomalhaut","Fornax","Fredux","Galatia","Gamma","Gauss","Gamorae","Gemini","Gerd","Geinah","Gnur","Gomeisa","Gorsu","Grus","Guiness","Gungnir","Hadar","Halley","Hamal","Hawking","Hector","Heisenberg","Hera","Hercules","Hephiastes","Hispalis","Hodge","Hofstader","Holly","Hopper","Horae","Hospodar","Hubble","Hyades","Hydrus","Hyperion","Iberia","Iki","Innana","Interigon","Iota","Indus","Inus","Ishtar","Iswar","Izahami","Jade","Janus","Jhurna","Jocasta","Jones","Jove","Jupiter","Kappa","Kaus","Keats","Kelsi","Kenobi","Kirk","Kiltis","Klingon","Kocab","Koivikko","Kornophoros","Kryten","Lafka","Lamarna","Leia","Leo","Lepus","Lesath","Libra","Lister","Lucas","Lugdunensis","Lupus","Lynx","Lyra","Magellan","Malaca","Mars","Mayhew","McCaffery","Melitene","Mercury","Mesuta","Megnazon","Memphis","Merak","Meukalinan","Miaplacidus","Mimosa","Mirach","Mizar","Moesia","Monoceros","Mordor","Muphrid","Musca","Narbonensis","Nemesis","Neptune","Nessus","Newton","Noricum","Nuada","Numidia","Nunka","Nysvaekoto","Nyx","Og","Omega","Omicron","Oox","Ophiucus","Orion","Otski","Palmyra","Pannonia","Pavo","Peace","Pegasus","Pelops","Perseus","Petra","Phakt","Pheonix","Phi","Picard","Pictor","Pisces","Planck","Pleides","Pluto","Polaris","Procyon","Psi","Ptolemy","Puppis","Pyxis","Qrdus","Quadran","Quipus","Rantsu","Rastaban","Regulus","Remus","Reticulum","Rhaetia","Rho","Rigel","Riker","Rimmer","Rompf","Rosenberg","Rotarev","Saberhagen","Sadalrud","Sagittarius","Salamis","Sandage","Sargas","Saturn","Scorpio","Scrolli","Segusio","Seleusia","Servi","Shapley","Sheat","Sigma","Simmons","Sinope","Sirius","Skywalker","Sol","Spica","Spock","Sulu","Suomi","Susa","Taeffae","Tammuz","Tarazed","Taurus","Teller","Tertsix","Thospia","Theta","Thuban","Trinus","Tolkien","Triangulum","Tucana","Umbriel","Unukalkay","Upsilon","Uranus","Ursa","Urth","Vader","Valentia","Vedas","Vega","Vela","Venatici","Virgo","Volans","War","Wesen","Williams","Wirth","Woosley","Worf","Xi","Yima","Yoda","Yuggoth","Yrga","Zarg","Zawijah","Zeus","Zot","Zog","Zozca","Zeta","Zuben"
        )

        // Planets
        // Planets from: https://github.com/kaladron/galactic-bloodshed/blob/master/data/planets.list
        private val planetNames = arrayOf(
            "Aasarus", "Aberius", "Abyssinia", "Aceldama", "Achates", "Acheron", "Achilles", "Acropolis", "Acrisis", "Adam", "Adrastea", "Adreaticus", "Aeetes", "Aegea", "Aegir", "Aeneas", "Aeolus", "Aesir", "Aesop", "Aether", "Agamemnon", "Agnar", "Agni", "Aiakos", "Ajax", "Akroteri", "Albion", "Alcmene", "Alexander", "Alfheim", "Amalthea", "Amaterasu", "Americos", "Amphion", "Anacreon", "Anchises", "Angakok", "Angurboda", "Ansu", "Antaeus", "Antigone", "Anubis", "Apache", "Apaosha", "Aphrodite", "Apsyrtus", "Aragorn", "Arapaho", "Argos", "Argus", "Ariadne", "Arion", "Armageddon", "Asgard", "Ashur", "Astarte", "Athens", "Atlantis", "Atlas", "Atropos", "Attica", "Audhumla", "Avalon", "Azazel", "AziDahak", "Azores", "Baal", "Babylon", "Bacchus", "Baezelbub", "Bagdemagus", "Balder", "Bakongo", "Bali", "Ban", "Baugi", "Bedivere", "Belial", "Bellerophon", "Bellona", "Benkei", "Bergelmir", "Bernoulli", "Berserks", "Bianca", "Bifrost", "Bohr", "Boeotia", "Bohort", "Bora", "Bors", "Boz", "Bragi", "Brave", "Brisingamen", "Britannia", "Buri", "Bushido", "Cadiz", "Caeser", "Cain", "Camelot", "Camlan", "Canan", "Carbonek", "Carme", "Carthage", "Celaeno", "Celephais", "Celeus", "Celt", "Cerberus", "Cetelus", "Ceto", "Cheng-huang", "Chansky", "Chaos", "Charon", "Charybdis", "Cheiron", "Cherubim", "Chi'ih-yu", "Chimera", "Chiron", "Chow", "Chuai", "Cirrus", "Claire", "Cleito", "Clotho", "Clytemnestra", "Cocytus", "Colchis", "Coniraya", "Cordelia", "Corinth", "Coriolis", "Cranus", "Crassus", "Cressida", "Cronus", "Cyclops", "Cyzicus", "Daedalus", "Dagon", "Damon", "Danae", "Danaus", "Dannaura", "Danube", "Dardanus", "Darkalheim", "Darwin", "Dawn", "Deganus", "Deimus", "Delphi", "Demeter", "Demise", "Demogorgon", "Depth", "Descartes", "Desdemona", "Desire", "Desolation", "Despoina", "Deucalion", "Devadatta", "Dharma", "Diana", "Dinka", "Diogenes", "Dis", "Djanggawul", "Dorian", "Dorus", "Doula", "Dove", "Dragon", "Draupnir", "Drak", "Dream", "Druj", "Dwarka", "Dyaus", "Earth", "Echo", "Ectoprimax", "Ector", "Eddas", "Edjo", "Eegos", "Ehecatl", "Einstein", "Elara", "Eldorado", "Electra", "Eleusis", "Ellindil", "Elysia", "Enceladus", "Endoprimax", "Enkindu", "Enlil", "Ephialtes", "Ephraites", "Epigoni", "Epimetheus", "Erebus", "Erech", "Ergos", "Erishkegal", "Eros", "Estanatleh", "Eteocles", "Eurasia", "Euripedes", "Europa", "Eurydice", "Eurytion", "Excalibur", "Exodus", "Falcha", "Februs", "FeiLien", "Fenrir", "FonLegba", "FourthWorld", "Freedom", "Freuchen", "Freyr", "Frigg", "Frisia", "Forseti", "Fu'hsi", "Fujin", "Future", "Gabora", "Gades", "Gadir", "Gaea", "Galactica", "Galahad", "Galanopoulus", "Galileo", "Gandalf", "Ganymede", "Garm", "Gaokerena", "Gathas", "Gawain", "Geirrod", "Gerda", "Germania", "Geryon", "Ghal", "Gil-galad", "Gilgamesh", "Gimli", "Ginnungagap", "Gjallarhorn", "Glispa", "Gondor", "Gorgias", "Gorgon", "Gorlois", "Gosadaya", "Graeae", "Granis", "Grunbunde", "Guinevere", "Hadad", "Halloway", "Halvec", "Haoma", "Harpie", "Haroeris", "Hathor", "Heaven", "Hebe", "Hecate", "Hegel", "Hei-tiki", "Heidrun", "Heidegger", "Heimdall", "Heisenburg", "Hel", "Helene", "Hell", "Helle", "Heracles", "Hermod", "Hesoid", "Hestia", "Himalia", "Hodur", "Hoeinir", "Homer", "Hope", "Hotei", "Hringhorni", "Hugi", "Huron", "Hyksos", "Hyperion", "Iacchos", "Iapetus", "Ibanis", "Icarus", "Idun", "Illiam", "Illyria", "Imperium", "Infinity", "Iolcus", "Ioue", "Irkalla", "Ismene", "Iulus", "Ixbalanque", "Ixmirsis", "Jackal", "Jamsid", "Jataka", "Jazdarnil", "Jena", "Jotars", "Jotunheim", "Juliet", "Jung", "Jurojin", "Ku'unLun", "Kachinas", "Kadath", "Kalki", "Kamakura", "Kant", "Karaja", "Karnak", "Kay", "Kepa", "Keshvar", "Kettoi", "Khepri", "Khwanirath", "Kierkegaard", "Kirin", "Kishijoten", "Koffler", "Kol", "Kunmanggur", "Kuya-Shonin", "Kythamil", "Labyrinth", "Lachesis", "Laindjung", "Laius", "Lancelot", "Lao-tze", "Lapiths", "Lares", "Larissa", "Larnok", "Leda", "Leibniz", "Leinster", "Lemnos", "Leng", "Leodegrance", "Lethe", "Leto", "Leviathan", "Lhard", "Lodbrok", "Lodur", "Logan", "Logi", "Loki", "Lot", "Lotan", "Love", "Lugundum", "Lungshan", "Luntag", "Luyia", "Lycurgus", "Lysithea", "Macha", "Mador", "Maenads", "Magna", "Mandara", "Manitou", "Marduk", "Marmora", "Mars", "Mashyane", "Medea", "Medusa", "Meepzorp", "Megara", "Meneleus", "Menrua", "Mercury", "Merlin", "Metis", "Midas", "Midgard", "Mimas", "Mimir", "Minoa", "Minos", "Miranda", "Miriam", "Mirkwood", "Mithrandir", "Mjolnir", "Modred", "Moirai", "Montaigne", "Morrigan", "Mulungu", "Munin", "Muspellheim", "Nada", "Nagilfar", "Naiad", "Nanna", "Narcissus", "Nardis", "Nebo", "Nelligan", "Neptune", "Nereid", "Nietszche", "Night", "Ninhursag", "Ninigi", "Nirvana", "Njord", "Norn", "Nu-ku", "Nuba", "Nuptadi", "Nyambe", "Nyarlathotep", "Nymph", "Oberon", "Obolus", "Oceana", "Od", "Odin", "Odysseus", "Oedipus", "Oeneus", "Ohrmazd", "Ojin", "Okesa", "Okokanoffee", "Okuninushi", "Olifat", "Oliphant", "Olympus", "Ophelia", "Orchomenus", "Ord", "Orestes", "Orpheus", "Oro", "Orthoprimax", "Orunila", "Osirus", "Otus", "Ovid", "Oz", "P'an-Ku", "Pachacutil", "Pan", "Pandora", "Parshya", "Parvati", "Pasiphae", "Patala", "Patrise", "Pelias", "Pellas", "Pellinore", "Peloponnesus", "Pek", "Percivale", "Persphone", "Phegethon", "Phineas", "Phobos", "Phoebe", "Phoenix", "Phorcys", "Phrixus", "Phyrro", "Piithar", "Pindar", "Pinel", "Pifall", "Plato", "Pluto", "Polybus", "Polydectes", "Polyneices", "Poseiden", "Priam", "Primax", "Prithivi", "Prometheus", "Proetus", "Pthura", "Ptolemy", "Puck", "Pymeria", "Pyrgos-Dirou", "Pyrrha", "Pythia", "Qa", "Qwaai", "QuagKeep", "Quetzalcoatl", "Quimbaya", "Quirnok", "R'lyeh", "Radha", "Ragnarok", "Rashnu", "Rata", "Rauta42", "Rauta5-0", "Rauta22", "Rauta69", "Rauta26", "Rauta99", "Reggae", "Regia", "Regulus", "Resida", "Rhadamanthys", "Rhea", "Rhpisort", "Rimahad", "Rol", "Rosalind", "Rousseau", "Rudra", "Ruhe", "Rukmini", "Runes", "Rustam", "Ryobu", "Sagurak", "Sakra", "Salacia", "Sappedon", "Sardonis", "Sarnak", "Satan", "Sati", "Sauron", "Scearce", "Schopenhaur", "Scylla", "Secundus", "Sedaria", "Sedna", "Sega", "Sekasis", "Semele", "Seneca", "Seriphos", "Sha-Lana", "Shaman", "Shantak", "Shapash", "Shax", "Shen", "Siddhartha", "Sif", "Sig", "Sigyn", "Sinope", "Sirens", "Skidbladnir", "Skirnir", "Skrymir", "Skuld", "Sleipner", "Socrates", "Solaria", "Sphinx", "Spinner", "Spinoza", "Srishok", "Styx", "Suboceana", "Suga", "Sugriva", "Surt", "Surya", "Swarga", "Symplegades", "Syrinx", "T'aishan", "Tacitus", "Tadanobu", "Tantrus", "Tare", "Tarnesus", "Tartarus", "Taweskare", "Tekeli-li", "Telesto", "Terminus", "Terra", "Tethys", "Thalassa", "Thebes", "Themis", "Thera", "Theseus", "Thinis", "Thjalfi", "Thjazi", "Thor", "Thought", "Thrace", "Thrasymachus", "Ti'i", "Tigra-nog'th", "Tiki", "Tillich", "Timaeus", "Time", "Tirawa-Atius", "Titan", "Tiu", "Tjinimin", "Tou-Mu", "Trantor", "Trashtri", "Trisala", "Triton", "Troll", "Troy", "Tsathoggua", "Tsentsa", "Typhon", "Tyr", "Tyrns", "Tyrrea", "Uhura", "Ukaipu", "Ulka", "Unicorn", "Unok", "Upuaut", "Uranus", "Urd", "Urdanis", "Urech", "Ursa", "Ursharabi", "Urth", "Utgard", "Uther", "Utnapishtim", "Uxmal", "Val", "Valhalla", "Valkyrie", "VanCleef", "Vanaheim", "Vanir", "Varnak", "Vasuki", "Ve", "Vedies", "Venus", "Verdandi", "Vesta", "Vigari", "Vili", "Virag", "Vivian", "Vishtaspa", "VohuManuh", "Voltaire", "Volva", "Vucu'Caquix", "Vulcan", "Vurat", "Walla", "Wampum", "Waterloo", "Waura", "Wawalag", "Woden", "Wu", "Xantor", "Xerxes", "Xrka", "Yaddith", "Yami", "Yamotodake", "Yang", "Yangtse", "Yarnek", "Ygerne", "Yggdrasil", "Yin", "Ymir", "Yog-sothoth", "Yu-Nu", "Yu-ti", "Yucatan", "YumCaax", "Zaaxon", "Zahak", "Zapana", "Zarathrusta", "Zetes", "Zethus", "Zocho", "Zoroaster", "Zrd", "Zurvan"
        )

        // Types from: http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/
        private val planetTypesNames =
            arrayOf("M Class", "Jovian", "Water", "Desert", "Forest", "Iceball", "Airless", "Asteroid")

        private val planetTypesSize = arrayOf(
            // min height, max height. Width will be 2x height
            intArrayOf(4, 6), // M Class
            intArrayOf(5, 7), // Jovian
            intArrayOf(4, 6), // Water
            intArrayOf(4, 6), // Desert
            intArrayOf(3, 5), // Forest
            intArrayOf(3, 4), // Iceball
            intArrayOf(3, 5), // Airless
            intArrayOf(2, 3)  // Asteroid
        )

        // Sectors
        // Planets are rectangles of sectors with wrap-arounds on the sides. Think Mercator.
        // Sector Types from: http://web.archive.org/web/20060501033212/http://monkeybutts.net:80/games/gb/
        // Source had 7 types, added 8th type "rock" because I had a bitmap, and because asteroids and mountains are different
        //private val sectorTypesNames = arrayOf("Water", "Land", "Gas", "Desert", "Mountain", "Forest", "Ice", "Rock")
        private val sectorTypesConsole =
            arrayOf("~", ".", "@", "-", "^", "*", "#", "x") // TODO double check these against source
        private val sectorTypesChance = arrayOf(
            intArrayOf(0, 0, 0, 0, 1, 1, 1, 1, 5, 6), // M Class
            intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2, 2), // Jovian
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 4), // Water
            intArrayOf(3, 3, 3, 3, 3, 3, 3, 3, 7, 7), // Desert
            intArrayOf(5, 5, 5, 5, 5, 5, 5, 5, 5, 1), // Forest
            intArrayOf(7, 7, 6, 6, 6, 6, 6, 6, 6, 6), // Iceball
            intArrayOf(1, 7, 7, 3, 3, 3, 4, 4, 4, 6), // Airless
            intArrayOf(7, 7, 7, 7, 7, 7, 7, 7, 6, 6)  // Asteroid
        )
    }


}