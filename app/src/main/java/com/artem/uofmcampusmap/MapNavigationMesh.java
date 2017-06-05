package com.artem.uofmcampusmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-05-30.
 */

public class MapNavigationMesh
{
    private ArrayList<WalkableZone> walkableZones;
    private HashMap<String, ArrayList<Vertex>> startEndLocations;

    public MapNavigationMesh()
    {
        walkableZones = new ArrayList<>();
        startEndLocations = new HashMap<>();
        populationMesh();
    }
    private void addEntrance(String keyName, Vertex entranceVertex)
    {
        ArrayList<Vertex> entrancesList;

        if(startEndLocations.containsKey(keyName))
        {
            entrancesList = startEndLocations.get(keyName);
        }
        else
        {
            entrancesList = new ArrayList<>();
        }

        entrancesList.add(entranceVertex);
        startEndLocations.put(keyName, entrancesList);
    }

    public Route findRoute(String startLocation, String endLocation)
    {
        Route route = null;
        Route prevRoute;
        ArrayList<Vertex> entrances;
        ArrayList<Vertex> exits;
        RouteFinder routeFinder = new RouteFinder();

        if(startEndLocations.containsKey(startLocation) && startEndLocations.containsKey(endLocation))
        {
            entrances = startEndLocations.get(startLocation);
            exits = startEndLocations.get(endLocation);

            //Find the shortest path from all entrances of a building to another buildings exits
            for(Vertex currEntrance: entrances)
            {
                for(Vertex currExit: exits)
                {
                    prevRoute = routeFinder.findRoute(currEntrance, currExit);

                    if(route == null)
                    {
                        route = prevRoute;
                    }
                    else if(prevRoute != null && prevRoute.currRouteQuicker(route))
                    {
                        route = prevRoute;
                    }
                }
            }
        }

        return route;
    }

    private void populationMesh()
    {
        //BottomMiddle is the entrancce
        WalkableZone agri_engineer_north_ent = new WalkableZone(new LatLng(49.807610, -97.133883), new LatLng(49.807633, -97.133787), new LatLng(49.807471, -97.133811), new LatLng(49.807485, -97.133747));
        agri_engineer_north_ent.setConnectors(null, new LatLng(49.807577, -97.133863), new LatLng(49.807589, -97.133770), new LatLng(49.807491, -97.133790));

        addEntrance("Agricultural Engineering", agri_engineer_north_ent.getBottomMiddle());

        //BottomMiddle is the entrance
        WalkableZone agri_engineer_south_ent = new WalkableZone(new LatLng(49.807475, -97.134234), new LatLng(49.807495, -97.134172), new LatLng(49.807396, -97.134163), new LatLng(49.807409, -97.134103));
        agri_engineer_south_ent.setConnectors(null, new LatLng(49.807456, -97.134232), new LatLng(49.807472, -97.134153), new LatLng(49.807406, -97.134132));

        addEntrance("Agricultural Engineering", agri_engineer_north_ent.getBottomMiddle());

        //BottomMiddle is the entrance
        WalkableZone agriculture_north_ent = new WalkableZone(new LatLng(49.807164, -97.135187), new LatLng(49.807193, -97.135104), new LatLng(49.807080, -97.135103), new LatLng(49.807106, -97.135021));
        agriculture_north_ent.setConnectors(null, new LatLng(49.807141, -97.135138), new LatLng(49.807172, -97.135082), new LatLng(49.807100, -97.135065));

        addEntrance("Agriculture", agriculture_north_ent.getBottomMiddle());

        //BottomMiddle is the entrance
        WalkableZone agriculture_south_ent = new WalkableZone(new LatLng(49.807019, -97.135584), new LatLng(49.807036, -97.135521), new LatLng(49.806936, -97.135529), new LatLng(49.806952, -97.135486));
        agriculture_south_ent.setConnectors(null, new LatLng(49.806997, -97.135575), new LatLng(49.807014, -97.135511), new LatLng(49.806958, -97.135498));

        addEntrance("Agriculture", agriculture_south_ent.getBottomMiddle());

        //RightMiddle is entrance
        WalkableZone allen_armes_buller = new WalkableZone(new LatLng(49.810959, -97.134638), new LatLng(49.811028, -97.134368), new LatLng(49.810859, -97.134555), new LatLng(49.810940, -97.134297));
        allen_armes_buller.setConnectors(null, new LatLng(49.810950, -97.134629), new LatLng(49.810993, -97.134347), null);

        addEntrance("Allen", allen_armes_buller.getRightMiddle());
        addEntrance("Armes", allen_armes_buller.getRightMiddle());
        addEntrance("Buller", allen_armes_buller.getRightMiddle());

        //rightMiddle is entrance
        WalkableZone allen_armes = new WalkableZone(new LatLng(49.810574, -97.134299), new LatLng(49.810651, -97.134069), new LatLng(49.810492, -97.134241), new LatLng(49.810576, -97.134008));
        allen_armes.setConnectors(null, new LatLng(49.810525, -97.134266), new LatLng(49.810614, -97.134037), null);

        addEntrance("Allen", allen_armes.getRightMiddle());
        addEntrance("Armes", allen_armes.getRightMiddle());

        //bottomMiddle is entrance
        WalkableZone animal_sci_north_ent = new WalkableZone(new LatLng(49.806270, -97.137785), new LatLng(49.806324, -97.137599), new LatLng(49.806171, -97.137650), new LatLng(49.806203, -97.137552));
        animal_sci_north_ent.setConnectors(null, new LatLng(49.806251, -97.137814), new LatLng(49.806315, -97.137570), new LatLng(49.806214, -97.137599));

        addEntrance("Animal Science / Entomology", animal_sci_north_ent.getBottomMiddle());

        //bottomMiddle is entrance
        WalkableZone animal_sci_south_ent = new WalkableZone(new LatLng(49.806110, -97.138247), new LatLng(49.806138, -97.138146), new LatLng(49.806023, -97.138121), new LatLng(49.806044, -97.138058));
        animal_sci_south_ent.setConnectors(null, new LatLng(49.806112, -97.138202), new LatLng(49.806138, -97.138101), new LatLng(49.806032, -97.138085));

        addEntrance("Animal Science / Entomology", animal_sci_south_ent.getBottomMiddle());

        //bottomMiddle is entrance
        WalkableZone archi2_north_ent = new WalkableZone(new LatLng(49.808076, -97.136340), new LatLng(49.808090, -97.136241), new LatLng(49.807958, -97.136240), new LatLng(49.807985, -97.136136));
        archi2_north_ent.setConnectors(new LatLng(49.808076, -97.136310), null, new LatLng(49.808017, -97.136158), new LatLng(49.807970, -97.136214));

        addEntrance("Architecture 2", archi2_north_ent.getBottomMiddle());

        //topMiddle is entrance
        WalkableZone archi2_south_ent = new WalkableZone(new LatLng(49.807778, -97.136417), new LatLng(49.807816, -97.136319), new LatLng(49.807708, -97.136326), new LatLng(49.807741, -97.136225));
        archi2_south_ent.setConnectors(new LatLng(49.807786, -97.136334), null, null, new LatLng(49.807725, -97.136278));

        addEntrance("Architecture 2", archi2_south_ent.getBottomMiddle());

        //topMiddle is entrance
        WalkableZone armes_machray = new WalkableZone(new LatLng(49.810977, -97.133496), new LatLng(49.811063, -97.133218), new LatLng(49.810778, -97.133302), new LatLng(49.810857, -97.133063));
        armes_machray.setConnectors(new LatLng(49.810997, -97.133403), null, new LatLng(49.811023, -97.133166), null);

        addEntrance("Armes", armes_machray.getTopMiddle());
        addEntrance("Machray", armes_machray.getTopMiddle());

        //rightMiddle is entrance
        WalkableZone artlab = new WalkableZone(new LatLng(49.808630, -97.130596), new LatLng(49.808680, -97.130466), new LatLng(49.808323, -97.130325), new LatLng(49.808332, -97.130279));
        artlab.setConnectors(new LatLng(49.808661, -97.130542), null, new LatLng(49.808648, -97.130393), new LatLng(49.808326, -97.130296));

        addEntrance("Artlab", artlab.getRightMiddle());

        //leftMiddle is entrancce
        WalkableZone bio_science_east_ent = new WalkableZone(new LatLng(49.810327, -97.134509), new LatLng(49.810361, -97.134390), new LatLng(49.810240, -97.134439), new LatLng(49.810274, -97.134310));
        bio_science_east_ent.setConnectors(new LatLng(49.810332, -97.134465), new LatLng(49.810287, -97.134455), new LatLng(49.810312, -97.134349), null);

        addEntrance("Biological Science", bio_science_east_ent.getLeftMiddle());

        //rightMiddle is entrance
        WalkableZone bio_science_west_ent = new WalkableZone(new LatLng(49.810106, -97.135083), new LatLng(49.810126, -97.135071), new LatLng(49.810045, -97.135043), new LatLng(49.810066, -97.135023));
        bio_science_west_ent.setConnectors(new LatLng(49.810110, -97.135071), null, new LatLng(49.810090, -97.135044), null);

        addEntrance("Biological Science", bio_science_east_ent.getRightMiddle());

        //topMiddle is entrance
        WalkableZone buller_west_ent = new WalkableZone(new LatLng(49.810325, -97.133594), new LatLng(49.810342, -97.133531), new LatLng(49.810283, -97.133552), new LatLng(49.810300, -97.133482));
        buller_west_ent.setConnectors(new LatLng(49.810327, -97.133548), null, null, new LatLng(49.810288, -97.133519));

        addEntrance("Buller", buller_west_ent.getTopMiddle());

        //topMiddle is entrance
        WalkableZone buller_east_ent = new WalkableZone(new LatLng(49.810452, -97.133216), new LatLng(49.810471, -97.133141), new LatLng(49.810408, -97.133167), new LatLng(49.810427, -97.133103));
        buller_east_ent.setConnectors(new LatLng(49.810459, -97.133175), null, null, new LatLng(49.810418, -97.133134));

        addEntrance("Buller", buller_east_ent.getTopMiddle());

        //bottomMiddle is entrance
        WalkableZone dairy_science = new WalkableZone(new LatLng(49.807725, -97.133415), new LatLng(49.807743, -97.133356), new LatLng(49.807632, -97.133349), new LatLng(49.807653, -97.133286));
        dairy_science.setConnectors(null, new LatLng(49.807709, -97.133405), new LatLng(49.807709, -97.133405), new LatLng(49.807654, -97.133320));

        addEntrance("Dairy Science", dairy_science.getBottomMiddle());

        //bottomMiddle is entrance
        WalkableZone drake = new WalkableZone(new LatLng(49.808328, -97.130346), new LatLng(49.808348, -97.130283), new LatLng(49.808202, -97.130248), new LatLng(49.808226, -97.130175));
        drake.setTopMiddle(artlab.getBottomMiddle());
        drake.setBottomMiddle(new LatLng(49.808213, -97.130217));

        addEntrance("Drake", drake.getBottomMiddle());

        //rightMid
        WalkableZone duff_roblin_west_ent = new WalkableZone(new LatLng(49.810975, -97.133121), new LatLng(49.810993, -97.133068), new LatLng(49.810749, -97.132958), new LatLng(49.810776, -97.132903));
        duff_roblin_west_ent.setConnectors(new LatLng(49.810981, -97.133100), null, new LatLng(49.810894, -97.132999), new LatLng(49.810765, -97.132917));

        addEntrance("Duff Roblin", duff_roblin_west_ent.getRightMiddle());

        //topMid
        WalkableZone duff_roblin_south_ent = new WalkableZone(new LatLng(49.810911, -97.132473), new LatLng(49.810993, -97.132212), new LatLng(49.810885, -97.132438), new LatLng(49.810967, -97.132194));
        duff_roblin_south_ent.setConnectors(new LatLng(49.810961, -97.132287), new LatLng(49.810904, -97.132436), null, null);

        addEntrance("Duff Roblin", duff_roblin_south_ent.getTopMiddle());

        //topMid
        WalkableZone education_south_ent = new WalkableZone(new LatLng(49.808603, -97.136485), new LatLng(49.808730, -97.136084), new LatLng(49.808546, -97.136412), new LatLng(49.808668, -97.136053));
        education_south_ent.setConnectors(new LatLng(49.808624, -97.136406), null, new LatLng(49.808696, -97.136074 ), new LatLng(49.808606, -97.136228));

        addEntrance("education", education_south_ent.getTopMiddle());

        //rightMid
        WalkableZone education_west_ent = new WalkableZone(new LatLng(49.808459, -97.137405), new LatLng(49.808490, -97.137321), new LatLng(49.808391, -97.137367), new LatLng(49.808426, -97.137259));
        education_west_ent.setConnectors(null, null, new LatLng(49.808452, -97.137281), new LatLng(49.808409, -97.137312));

        addEntrance("Education", education_west_ent.getRightMiddle());

        //leftMid
        WalkableZone education_north_ent = new WalkableZone(new LatLng(49.809193, -97.136623), new LatLng(49.809237, -97.136483), new LatLng(49.809045, -97.136514), new LatLng(49.809091, -97.136383));
        education_north_ent.setConnectors(null, new LatLng(49.809142, -97.136583), new LatLng(49.809199, -97.136452), null);

        addEntrance("Education", education_north_ent.getLeftMiddle());

        //leftMid
        WalkableZone eitc_e1_east_ent = new WalkableZone(new LatLng(49.808477, -97.133103), new LatLng(49.808586, -97.132744), new LatLng(49.808452, -97.133072), new LatLng(49.808553, -97.132706));
        eitc_e1_east_ent.setConnectors(new LatLng(49.808571, -97.132774), new LatLng(49.808453, -97.133077), null, new LatLng(49.808453, -97.133077));

        addEntrance("EITC E1", eitc_e1_east_ent.getLeftMiddle());

        //topMid
        WalkableZone eitc_e1_e3 = new WalkableZone(new LatLng(49.808217, -97.134071), new LatLng(49.808257, -97.133959), new LatLng(49.807773, -97.133753), new LatLng(49.807830, -97.133600));
        eitc_e1_e3.setConnectors(new LatLng(49.808243, -97.133994), new LatLng(49.807797, -97.133749), new LatLng(49.807840, -97.133618), null);

        addEntrance("EITC E1", eitc_e1_e3.getTopMiddle());
        addEntrance("EITC E3", eitc_e1_e3.getTopMiddle());

        //leftMid
        WalkableZone eitc_e1_e2 = new WalkableZone(new LatLng(49.808665, -97.133268), new LatLng(49.808786, -97.132890), new LatLng(49.808637, -97.133238), new LatLng(49.808763, -97.132867));
        eitc_e1_e2.setConnectors(new LatLng(49.808772, -97.132921), new LatLng(49.808648, -97.133257), null, new LatLng(49.808745, -97.132906));

        addEntrance("EITC E1", eitc_e1_e2.getLeftMiddle());
        addEntrance("EITC E2", eitc_e1_e2.getLeftMiddle());

        //bottomMid
        WalkableZone eitc_e2 = new WalkableZone(new LatLng(49.808984, -97.133486), new LatLng(49.809029, -97.133355), new LatLng(49.808951, -97.133464), new LatLng(49.808962, -97.133295));
        eitc_e2.setConnectors(new LatLng(49.809005, -97.133424), null, new LatLng(49.808996, -97.133327), new LatLng(49.808937, -97.133421));

        addEntrance("EITC E2", eitc_e2.getBottomMiddle());

        //bottomMid
        WalkableZone eitc_e3 = new WalkableZone(new LatLng(49.808696, -97.134841), new LatLng(49.809083, -97.133571), new LatLng(49.808552, -97.134754), new LatLng(49.808988, -97.133486));
        eitc_e3.setConnectors(null, new LatLng(49.808633, -97.134836), new LatLng(49.809034, -97.133540), new LatLng(49.808643, -97.134489));

        addEntrance("EITC E3", eitc_e3.getBottomMiddle());

        //topMid
        WalkableZone eli_dafoe = new WalkableZone(new LatLng(49.810011, -97.132205), null, new LatLng(49.809633, -97.131902), new LatLng(49.809925, -97.131626));
        eli_dafoe.setConnectors(new LatLng(49.809993, -97.132140), null, new LatLng(49.809935, -97.131675), new LatLng(49.809667, -97.131876));

        addEntrance("Elizabeth Dafoe", eli_dafoe.getTopMiddle());

        //rightMid
        WalkableZone ext_education_west_ent = new WalkableZone(new LatLng(49.807042, -97.139438), new LatLng(49.807129, -97.139223), new LatLng(49.806958, -97.139372), new LatLng(49.807047, -97.139160));
        ext_education_west_ent.setConnectors(new LatLng(49.807046, -97.139406), null, new LatLng(49.807073, -97.139269), null);

        addEntrance("Extended Education", ext_education_west_ent.getRightMiddle());

        //leftMid
        WalkableZone ext_education_east_ent = new WalkableZone(new LatLng(49.807704, -97.138029), new LatLng(49.807792, -97.137632), new LatLng(49.807476, -97.137869), new LatLng(49.807607, -97.137483));
        ext_education_east_ent.setConnectors(new LatLng(49.807774, -97.137696), new LatLng(49.807553, -97.137825), null, null);

        addEntrance("Extended Education", ext_education_east_ent.getLeftMiddle());

        //bottomMid
        WalkableZone fac_of_music_north_end = new WalkableZone(new LatLng(49.807664, -97.136246), new LatLng(49.807698, -97.136129), new LatLng(49.807333, -97.136082), new LatLng(49.807384, -97.135890));
        fac_of_music_north_end.setConnectors(new LatLng(49.807675, -97.136190), null, null, new LatLng(49.807369, -97.135997));

        addEntrance("Faculty of Music", fac_of_music_north_end.getBottomMiddle());

        //leftMid
        WalkableZone fac_of_music_south_end = new WalkableZone(new LatLng(49.807185, -97.135773), new LatLng(49.807202, -97.135710), new LatLng(49.807153, -97.135756), new LatLng(49.807190, -97.135667));
        fac_of_music_south_end.setConnectors(null, new LatLng(49.807171, -97.135752), new LatLng(49.807197, -97.135675), null);

        addEntrance("Faculty of Music", fac_of_music_south_end.getLeftMiddle());

        //rightMid
        WalkableZone fletcher = new WalkableZone(new LatLng(49.809539, -97.131706), new LatLng(49.809621, -97.131453), new LatLng(49.809394, -97.131575), new LatLng(49.809488, -97.131327));
        fletcher.setConnectors(null, new LatLng(49.809476, -97.131635), new LatLng(49.809565, -97.131352), null);

        addEntrance("Fletcher", fletcher.getRightMiddle());

        //topMid
        WalkableZone helen_glass = new WalkableZone(new LatLng(49.808812, -97.135812), new LatLng(49.808858, -97.135535), new LatLng(49.808681, -97.135794), new LatLng(49.808662, -97.135456));
        helen_glass.setConnectors(new LatLng(49.808810, -97.135583), new LatLng(49.808760, -97.135817), new LatLng(49.808709, -97.135467), new LatLng(49.808640, -97.135591));

        addEntrance("Helen Glass", helen_glass.getTopMiddle());

        //rightMid
        WalkableZone human_eco = new WalkableZone(new LatLng(49.810798, -97.132487), new LatLng(49.810821, -97.132430), new LatLng(49.810419, -97.132431), new LatLng(49.810506, -97.132222));
        human_eco.setConnectors(new LatLng(49.810810, -97.132460), null, new LatLng(49.810700, -97.132333), new LatLng(49.810425, -97.132402));

        addEntrance("Human Ecology", human_eco.getRightMiddle());

        //topMid
        WalkableZone isbister = new WalkableZone(new LatLng(49.809111, -97.130311), new LatLng(49.809136, -97.130213), new LatLng(49.808965, -97.130180), new LatLng(49.808989, -97.130105));
        isbister.setConnectors(new LatLng(49.809124, -97.130257), new LatLng(49.809064, -97.130248), null, new LatLng(49.808981, -97.130163));

        addEntrance("Isbister", isbister.getTopMiddle());

        //bottomMid
        WalkableZone parker = new WalkableZone(new LatLng(49.811353, -97.135809), new LatLng(49.811698, -97.134589), new LatLng(49.811321, -97.135811), new LatLng(49.811664, -97.134516));
        parker.setConnectors(new LatLng(49.811516, -97.135120), new LatLng(49.811335, -97.135811), null, new LatLng(49.811591, -97.134773));

        addEntrance("Parker", parker.getBottomMiddle());

        //rightMid
        WalkableZone robert_schultz_west_ent = new WalkableZone(new LatLng(49.810117, -97.136969), new LatLng(49.810235, -97.136765), new LatLng(49.810086, -97.136956), new LatLng(49.810187, -97.136725));
        robert_schultz_west_ent.setConnectors(new LatLng(49.810215, -97.136786), new LatLng(49.810097, -97.136940), new LatLng(49.810203, -97.136740), null);

        addEntrance("Robert Schultz Theatre", bio_science_west_ent.getRightMiddle());

        //topMid
        WalkableZone robert_schultz_south_ent = new WalkableZone(new LatLng(49.809909, -97.136491), new LatLng(49.809988, -97.136205), new LatLng(49.809813, -97.136425), new LatLng(49.809910, -97.136148));
        robert_schultz_south_ent.setConnectors(new LatLng(49.809943, -97.136340), new LatLng(49.809833, -97.136435), new LatLng(49.809922, -97.136160), null); //bottom won't matter due to no connections for parkade

        addEntrance("Robert Schultz Theatre", robert_schultz_south_ent.getTopMiddle());

        //rightMid
        WalkableZone robson = new WalkableZone(new LatLng(49.811811, -97.131165), new LatLng(49.811827, -97.131042), new LatLng(49.811731, -97.131095), new LatLng(49.811738, -97.130961));
        robson.setConnectors(new LatLng(49.811825, -97.131126), null, new LatLng(49.811758, -97.130976), null);

        addEntrance("Robson", robson.getRightMiddle());

        //botMid
        WalkableZone russel_north_ent = new WalkableZone(new LatLng(49.808332, -97.135622), new LatLng(49.808362, -97.135536), new LatLng(49.808178, -97.135498), new LatLng(49.808210, -97.135404));
        russel_north_ent.setConnectors(null, new LatLng(49.808290, -97.135584), new LatLng(49.808315, -97.135499), new LatLng(49.808192, -97.135455));

        addEntrance("Russel", russel_north_ent.getBottomMiddle());

        //topMid
        WalkableZone russel_south_ent = new WalkableZone(new LatLng(49.807892, -97.135264), new LatLng(49.807921, -97.135176), new LatLng(49.807418, -97.134878), new LatLng(49.807444, -97.134803));
        russel_south_ent.setConnectors(new LatLng(49.807905, -97.135216), null, null, new LatLng(49.807431, -97.134837));

        addEntrance("Russel", russel_south_ent.getTopMiddle());

        //rightMid
        WalkableZone st_johns_west_ent = new WalkableZone(new LatLng(49.810469, -97.137293), new LatLng(49.810568, -97.137053), new LatLng(49.810413, -97.137276), new LatLng(49.810545, -97.137009));
        st_johns_west_ent.setConnectors(new LatLng(49.810520, -97.137185), new LatLng(49.810438, -97.137287), new LatLng(49.810557, -97.137024), new LatLng(49.810486, -97.137138));

        addEntrance("St.Johns College", st_johns_west_ent.getRightMiddle());

        //botMid
        WalkableZone st_johns_north_ent = new WalkableZone(new LatLng(49.810817, -97.137225), new LatLng(49.810850, -97.137101), new LatLng(49.810714, -97.137174), new LatLng(49.810759, -97.137040));
        st_johns_north_ent.setConnectors(null, new LatLng(49.810760, -97.137195), null, new LatLng(49.810749, -97.137065));

        addEntrance("St.Johns College", st_johns_north_ent.getBottomMiddle());

        //leftMid
        WalkableZone st_johns_east_ent = new WalkableZone(new LatLng(49.811217, -97.136940), new LatLng(49.811264, -97.136789), new LatLng(49.810877, -97.136669), new LatLng(49.810910, -97.136532));
        st_johns_east_ent.setConnectors(null, new LatLng(49.811217, -97.136940), new LatLng(49.811236, -97.136876), new LatLng(49.810898, -97.136607));

        addEntrance("St.Johns College", st_johns_east_ent.getLeftMiddle());

        //botMid
        WalkableZone st_pauls_north_ent = new WalkableZone(new LatLng(49.810576, -97.138317), new LatLng(49.810650, -97.138129), new LatLng(49.810397, -97.138124), new LatLng(49.810463, -97.137942));
        st_pauls_north_ent.setConnectors(new LatLng(49.810584, -97.138266), null, null, new LatLng(49.810430, -97.138044));

        addEntrance("St.Pauls College", st_pauls_north_ent.getBottomMiddle());

        //leftMid
        WalkableZone st_pauls_east_ent = new WalkableZone(new LatLng(49.809905, -97.137418), new LatLng(49.810040, -97.137095), new LatLng(49.809859, -97.137385), new LatLng(49.810001, -97.137022));
        st_pauls_east_ent.setConnectors(null, new LatLng(49.809888, -97.137377), new LatLng(49.810018, -97.137080), new LatLng(49.809914, -97.137222));

        addEntrance("St.Pauls College", st_pauls_east_ent.getLeftMiddle());

        //rightMid
        WalkableZone tier_west_ent = new WalkableZone(new LatLng(49.809150, -97.131390), new LatLng(49.809223, -97.131163), new LatLng(49.809084, -97.131322), new LatLng(49.809143, -97.131097));
        tier_west_ent.setConnectors(new LatLng(49.809159, -97.131304), new LatLng(49.809095, -97.131370), new LatLng(49.809178, -97.131130), new LatLng(49.809110, -97.131273));

        addEntrance("Tier", tier_west_ent.getRightMiddle());

        //topMid
        WalkableZone tier_south_ent = new WalkableZone(new LatLng(49.808947, -97.130789), new LatLng(49.808961, -97.130703), new LatLng(49.808839, -97.130715), new LatLng(49.808857, -97.130621));
        tier_south_ent.setConnectors(new LatLng(49.808940, -97.130745), new LatLng(49.808867, -97.130741), null, new LatLng(49.808847, -97.130678));

        addEntrance("Tier", tier_south_ent.getTopMiddle());

        //rightMid
        WalkableZone uni_centre_west_ent = new WalkableZone(new LatLng(49.809210, -97.135372), new LatLng(49.809279, -97.135180), new LatLng(49.808874, -97.135086), new LatLng(49.808933, -97.134911));
        uni_centre_west_ent.setConnectors(null, null, new LatLng(49.809072, -97.135035), new LatLng(49.808910, -97.134955));

        addEntrance("University Centre", uni_centre_west_ent.getRightMiddle());

        //leftMid
        WalkableZone uni_centre_east_ent = new WalkableZone(new LatLng(49.809316, -97.133742), new LatLng(49.809422, -97.133583), new LatLng(49.809203, -97.133654), new LatLng(49.809302, -97.133481));
        uni_centre_east_ent.setConnectors(null, new LatLng(49.809291, -97.133712), new LatLng(49.809362, -97.133536), null);

        addEntrance("University Centre", uni_centre_east_ent.getLeftMiddle());

        //leftMid
        WalkableZone uni_centre_north_ent = new WalkableZone(new LatLng(49.809717, -97.134074), new LatLng(49.809757, -97.133985), new LatLng(49.809351, -97.133827), new LatLng(49.809370, -97.133664));
        uni_centre_north_ent.setConnectors(null, new LatLng(49.809455, -97.133905), new LatLng(49.809491, -97.133766), new LatLng(49.809349, -97.133790));

        addEntrance("University Centre", uni_centre_north_ent.getLeftMiddle());

        //topMid
        WalkableZone uni_college = new WalkableZone(new LatLng(49.811066, -97.131298), new LatLng(49.811142, -97.131053), new LatLng(49.810810, -97.131114), new LatLng(49.810893, -97.130871));
        uni_college.setConnectors(new LatLng(49.811055, -97.131105), new LatLng(49.810852, -97.131118), null, null);

        addEntrance("University College", uni_college.getTopMiddle());

        //topMid
        WalkableZone wallace = new WalkableZone(new LatLng(49.811675, -97.135725), new LatLng(49.811666, -97.135649), new LatLng(49.811611, -97.135740), new LatLng(49.811599, -97.135649));
        wallace.setConnectors(new LatLng(49.811658, -97.135693), null, null, new LatLng(49.811608, -97.135696));

        addEntrance("Wallace", wallace.getTopMiddle());

        //todo add all remaining paths
        WalkableZone a = new WalkableZone(new LatLng(49.808888, -97.130536), new LatLng(49.808990, -97.130103), new LatLng(49.808818, -97.130485), new LatLng(49.808962, -97.130078));
        a.setLeftMiddle(new LatLng(49.808853, -97.130508));
        a.setRightMiddle(isbister.getBottomMiddle());

        WalkableZone b = new WalkableZone(new LatLng(49.809080, -97.130257), new LatLng(49.809052, -97.130232), new LatLng(49.808884, -97.130531), new LatLng(49.808917, -97.130417));
        b.setRightMiddle(isbister.getLeftMiddle());
        b.setBottomMiddle(a.getLeftMiddle());

        //todo switch this to istbister tunnel entrance?
        WalkableZone c = new WalkableZone(new LatLng(49.808856, -97.130641), new LatLng(49.808900, -97.130525), new LatLng(49.808780, -97.130590), new LatLng(49.808820, -97.130469));
        c.setRightMiddle(a.getLeftMiddle());
        c.setTopMiddle(new LatLng(49.808840, -97.130624));
        c.setLeftMiddle(new LatLng(49.808813, -97.130605));

        WalkableZone d = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone e = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone f = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone g = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone h = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone i = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone j = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone k = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone l = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone m = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone n = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone o = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone p = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone q = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone r = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone s = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone t = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone u = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone v = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone w = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone x = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone y = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        WalkableZone z = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());

        // = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());
    }
}
