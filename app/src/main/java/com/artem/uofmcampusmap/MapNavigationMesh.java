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
        WalkableZone allen_armes_parker = new WalkableZone(new LatLng(49.810959, -97.134638), new LatLng(49.811028, -97.134368), new LatLng(49.810859, -97.134555), new LatLng(49.810940, -97.134297));
        allen_armes_parker.setConnectors(null, new LatLng(49.810950, -97.134629), new LatLng(49.810993, -97.134347), null);

        addEntrance("Allen", allen_armes_parker.getRightMiddle());
        addEntrance("Armes", allen_armes_parker.getRightMiddle());
        addEntrance("Parker", allen_armes_parker.getRightMiddle());

        //rightMiddle is entrance
        WalkableZone allen_armes = new WalkableZone(new LatLng(49.810501, -97.134556), new LatLng(49.810651, -97.134069), new LatLng(49.810408, -97.134489), new LatLng(49.810576, -97.134008));
        allen_armes.setConnectors(null, new LatLng(49.810449, -97.134508), new LatLng(49.810614, -97.134037), new LatLng(49.810471, -97.134296));

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
        dairy_science.setConnectors(null, new LatLng(49.807709, -97.133405), new LatLng(49.807729, -97.133342), new LatLng(49.807654, -97.133320));

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
        WalkableZone duff_roblin_south_ent = new WalkableZone(new LatLng(49.810807, -97.132793), new LatLng(49.810993, -97.132212), new LatLng(49.810747, -97.132744), new LatLng(49.810933, -97.132188));
        duff_roblin_south_ent.setConnectors(new LatLng(49.810961, -97.132287), new LatLng(49.810780, -97.132769), null, new LatLng(49.810875, -97.132454));

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
        fac_of_music_north_end.setConnectors(new LatLng(49.807675, -97.136190), new LatLng(49.807400, -97.136108), null, new LatLng(49.807369, -97.135997));

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

        WalkableZone uni_centre_south_west_ent = new WalkableZone(new LatLng(49.808773, -97.135190), new LatLng(49.808804, -97.135030), new LatLng(49.808773, -97.135073), new LatLng(49.808781, -97.135027));
        uni_centre_south_west_ent.setConnectors(new LatLng(49.808781, -97.135062), null, null, new LatLng(49.808778, -97.135052));

        //topMid
        WalkableZone uni_college = new WalkableZone(new LatLng(49.811066, -97.131298), new LatLng(49.811142, -97.131053), new LatLng(49.810810, -97.131114), new LatLng(49.810893, -97.130871));
        uni_college.setConnectors(new LatLng(49.811055, -97.131105), new LatLng(49.810852, -97.131118), null, null);

        addEntrance("University College", uni_college.getTopMiddle());

        //topMid
        WalkableZone wallace = new WalkableZone(new LatLng(49.811675, -97.135725), new LatLng(49.811666, -97.135649), new LatLng(49.811611, -97.135740), new LatLng(49.811599, -97.135649));
        wallace.setConnectors(new LatLng(49.811658, -97.135693), null, null, new LatLng(49.811608, -97.135696));

        addEntrance("Wallace", wallace.getTopMiddle());

        WalkableZone a = new WalkableZone(new LatLng(49.808888, -97.130536), new LatLng(49.808990, -97.130103), new LatLng(49.808818, -97.130485), new LatLng(49.808962, -97.130078));
        a.setLeftMiddle(new LatLng(49.808853, -97.130508));
        a.setRightMiddle(isbister.getBottomMiddle());

        WalkableZone b = new WalkableZone(new LatLng(49.809080, -97.130257), new LatLng(49.809052, -97.130232), new LatLng(49.808884, -97.130531), new LatLng(49.808917, -97.130417));
        b.setRightMiddle(isbister.getLeftMiddle());
        b.setBottomMiddle(a.getLeftMiddle());

        //todo switch this to istbister tunnel entrance?
        WalkableZone c = new WalkableZone(new LatLng(49.808856, -97.130641), new LatLng(49.808900, -97.130525), new LatLng(49.808780, -97.130590), new LatLng(49.808820, -97.130469));
        c.setRightMiddle(a.getLeftMiddle());
        c.setLeftMiddle(new LatLng(49.808811, -97.130605));

        WalkableZone d = new WalkableZone(new LatLng(49.808839, -97.130715), new LatLng(49.808857, -97.130621), new LatLng(49.808766, -97.130649), new LatLng(49.808792, -97.130572));
        d.setTopMiddle(tier_south_ent.getBottomMiddle());
        d.setRightMiddle(c.getLeftMiddle());
        d.setLeftMiddle(new LatLng(49.808804, -97.130670));

        WalkableZone e = new WalkableZone(new LatLng(49.808795, -97.130915), new LatLng(49.808839, -97.130715), new LatLng(49.808698, -97.130856), new LatLng(49.808766, -97.130649));
        e.setBottomMiddle(new LatLng(49.808758, -97.130674));
        e.setRightMiddle(d.getLeftMiddle());
        e.setLeftMiddle(new LatLng(49.808720, -97.130867));
        e.setTopMiddle(new LatLng(49.808804, -97.130796));

        WalkableZone f = new WalkableZone(new LatLng(49.808850, -97.130968), new LatLng(49.808916, -97.130771), new LatLng(49.808795, -97.130915), new LatLng(49.808839, -97.130715));
        f.setBottomMiddle(e.getTopMiddle());
        f.setTopMiddle(new LatLng(49.808855, -97.130921));
        f.setRightMiddle(tier_south_ent.getLeftMiddle());

        WalkableZone g = new WalkableZone(new LatLng(49.809096, -97.131293), new LatLng(49.809113, -97.131221), new LatLng(49.808836, -97.130941), new LatLng(49.808870, -97.130892));
        g.setTopMiddle(tier_west_ent.getBottomMiddle());
        g.setBottomMiddle(f.getTopMiddle());

        WalkableZone h = new WalkableZone(new LatLng(49.808701, -97.131180), new LatLng(49.808768, -97.130912), new LatLng(49.808616, -97.131096), new LatLng(49.808698, -97.130856));
        h.setTopMiddle(new LatLng(49.808704, -97.131109));
        h.setBottomMiddle(new LatLng(49.808620, -97.131126));
        h.setRightMiddle(e.getLeftMiddle());

        WalkableZone i = new WalkableZone(new LatLng(49.809277, -97.131604), new LatLng(49.809303, -97.131528), new LatLng(49.808687, -97.131189), new LatLng(49.808721, -97.131064));
        i.setTopMiddle(new LatLng(49.809287, -97.131564));
        i.setLeftMiddle(new LatLng(49.809017, -97.131397));
        i.setRightMiddle(tier_west_ent.getLeftMiddle());
        i.setBottomMiddle(h.getTopMiddle());

        WalkableZone j = new WalkableZone(new LatLng(49.809368, -97.131550), new LatLng(49.809367, -97.131519), new LatLng(49.809131, -97.131328), new LatLng(49.809156, -97.131268));
        j.setLeftMiddle(new LatLng(49.809350, -97.131538));
        j.setBottomMiddle(tier_west_ent.getTopMiddle());

        WalkableZone k = new WalkableZone(new LatLng(49.809329, -97.131642), new LatLng(49.809368, -97.131550), new LatLng(49.809281, -97.131610), new LatLng(49.809310, -97.131522));
        k.setTopMiddle(new LatLng(49.809341, -97.131608));
        k.setBottomMiddle(i.getTopMiddle());
        k.setRightMiddle(j.getLeftMiddle());

        WalkableZone l = new WalkableZone(new LatLng(49.809523, -97.131820), new LatLng(49.809539, -97.131706), new LatLng(49.809322, -97.131655), new LatLng(49.809404, -97.131531));
        l.setTopMiddle(new LatLng(49.809546, -97.131779));
        l.setRightMiddle(fletcher.getLeftMiddle());
        l.setLeftMiddle(new LatLng(49.809436, -97.131742));
        l.setBottomMiddle(k.getTopMiddle());

        WalkableZone m = new WalkableZone(new LatLng(49.809475, -97.131972), new LatLng(49.809523, -97.131820), new LatLng(49.809269, -97.131855), new LatLng(49.809322, -97.131655));
        m.setTopMiddle(l.getTopMiddle());
        m.setRightMiddle(l.getLeftMiddle());
        m.setLeftMiddle(new LatLng(49.809418, -97.131888));
        m.setBottomMiddle(new LatLng(49.809292, -97.131788));

        WalkableZone n = new WalkableZone(new LatLng(49.809673, -97.131926), new LatLng(49.809708, -97.131854), new LatLng(49.809525, -97.131816), new LatLng(49.809553, -97.131709));
        n.setTopMiddle(eli_dafoe.getBottomMiddle());
        n.setBottomMiddle(l.getTopMiddle());

        WalkableZone o = new WalkableZone(new LatLng(49.809461, -97.131987), new LatLng(49.809440, -97.131873), new LatLng(49.809412, -97.132013), new LatLng(49.809388, -97.131907));
        o.setLeftMiddle(new LatLng(49.809431, -97.131995));
        o.setRightMiddle(m.getLeftMiddle());

        WalkableZone p = new WalkableZone(new LatLng(49.809138, -97.132965), new LatLng(49.809470, -97.132029), new LatLng(49.809103, -97.132928), new LatLng(49.809412, -97.131975));
        p.setRightMiddle(o.getLeftMiddle());
        p.setTopMiddle(new LatLng(49.809147, -97.132927));
        p.setBottomMiddle(new LatLng(49.809264, -97.132436));

        WalkableZone q = new WalkableZone(new LatLng(49.809246, -97.132491), new LatLng(49.809283, -97.132380), new LatLng(49.809035, -97.132324), new LatLng(49.809065, -97.132216));
        q.setTopMiddle(p.getBottomMiddle());
        q.setLeftMiddle(new LatLng(49.809061, -97.132318));
        q.setRightMiddle(new LatLng(49.809080, -97.132241));

        WalkableZone r = new WalkableZone(new LatLng(49.809110, -97.132250), new LatLng(49.809110, -97.131920), new LatLng(49.809062, -97.132237), new LatLng(49.809080, -97.131949));
        r.setLeftMiddle(q.getRightMiddle());
        r.setRightMiddle(new LatLng(49.809092, -97.131945));

        WalkableZone s = new WalkableZone(new LatLng(49.809280, -97.131864), new LatLng(49.809302, -97.131721), new LatLng(49.809069, -97.131966), new LatLng(49.809056, -97.131868));
        s.setTopMiddle(m.getBottomMiddle());
        s.setLeftMiddle(r.getRightMiddle());
        s.setRightMiddle(new LatLng(49.809071, -97.131857));

        WalkableZone t = new WalkableZone(new LatLng(49.809059, -97.131885), new LatLng(49.809115, -97.131845), new LatLng(49.808940, -97.131700), new LatLng(49.808967, -97.131607));
        t.setTopMiddle(s.getRightMiddle());
        t.setBottomMiddle(new LatLng(49.808958, -97.131648));

        WalkableZone u = new WalkableZone(new LatLng(49.808951, -97.131746), new LatLng(49.809066, -97.131424), new LatLng(49.808889, -97.131679), new LatLng(49.808982, -97.131358));
        u.setTopMiddle(t.getBottomMiddle());
        u.setBottomMiddle(new LatLng(49.808901, -97.131617));
        u.setRightMiddle(i.getLeftMiddle());

        WalkableZone v = new WalkableZone(new LatLng(49.808905, -97.131687), new LatLng(49.808904, -97.131584), new LatLng(49.808743, -97.131658), new LatLng(49.808734, -97.131556));
        v.setTopMiddle(u.getBottomMiddle());
        v.setBottomMiddle(new LatLng(49.808735, -97.131593));

        WalkableZone w = new WalkableZone(new LatLng(49.808750, -97.131664), new LatLng(49.808690, -97.131130), new LatLng(49.808693, -97.131666), new LatLng(49.808621, -97.131143));
        w.setTopMiddle(v.getBottomMiddle());
        w.setBottomMiddle(new LatLng(49.808689, -97.131614));
        w.setRightMiddle(new LatLng(49.808651, -97.131208));

        WalkableZone x = new WalkableZone(new LatLng(49.808409, -97.131810), new LatLng(49.808613, -97.131228), new LatLng(49.808384, -97.131787), new LatLng(49.808581, -97.131196));
        x.setRightMiddle(new LatLng(49.808598, -97.131203));
        x.setTopMiddle(new LatLng(49.808436, -97.131724));
        x.setLeftMiddle(new LatLng(49.808397, -97.131796));

        WalkableZone y = new WalkableZone(new LatLng(49.808684, -97.131237), new LatLng(49.808701, -97.131180), new LatLng(49.808576, -97.131206), new LatLng(49.808617, -97.131090));
        y.setRightMiddle(h.getBottomMiddle());
        y.setBottomMiddle(x.getRightMiddle());
        y.setTopMiddle(h.getTopMiddle());
        y.setLeftMiddle(w.getRightMiddle());

        WalkableZone z = new WalkableZone(new LatLng(49.808704, -97.131674), new LatLng(49.808689, -97.131584), new LatLng(49.808426, -97.131773), new LatLng(49.808441, -97.131697));
        z.setTopMiddle(w.getBottomMiddle());
        z.setLeftMiddle(new LatLng(49.808577, -97.131720));
        z.setBottomMiddle(x.getTopMiddle());

        WalkableZone aa = new WalkableZone(new LatLng(49.808514, -97.132134), new LatLng(49.808596, -97.131762), new LatLng(49.808456, -97.132112), new LatLng(49.808543, -97.131733));
        aa.setLeftMiddle(new LatLng(49.808487, -97.132148));
        aa.setBottomMiddle(new LatLng(49.808468, -97.132026));
        aa.setRightMiddle(z.getLeftMiddle());

        WalkableZone ab = new WalkableZone(new LatLng(49.808467, -97.132115), new LatLng(49.808481, -97.131985), new LatLng(49.808367, -97.131835), new LatLng(49.808389, -97.131759));
        ab.setTopMiddle(aa.getBottomMiddle());
        ab.setLeftMiddle(new LatLng(49.808381, -97.131848));
        ab.setRightMiddle(x.getLeftMiddle());

        WalkableZone ac = new WalkableZone(new LatLng(49.808201, -97.132423), new LatLng(49.808390, -97.131871), new LatLng(49.808172, -97.132417), new LatLng(49.808371, -97.131816));
        ac.setLeftMiddle(new LatLng(49.808185, -97.132420));
        ac.setTopMiddle(new LatLng(49.808249, -97.132278));
        ac.setRightMiddle(ab.getLeftMiddle());

        WalkableZone ad = new WalkableZone(new LatLng(49.808628, -97.132565), new LatLng(49.808661, -97.132426), new LatLng(49.808451, -97.132165), new LatLng(49.808519, -97.132129));
        ad.setRightMiddle(aa.getLeftMiddle());
        ad.setBottomMiddle(new LatLng(49.808476, -97.132214));
        ad.setTopMiddle(new LatLng(49.808630, -97.132490));

        WalkableZone ae = new WalkableZone(new LatLng(49.808492, -97.132267), new LatLng(49.808484, -97.132161), new LatLng(49.808226, -97.132357), new LatLng(49.808260, -97.132244));
        ae.setTopMiddle(ad.getBottomMiddle());
        ae.setBottomMiddle(ac.getTopMiddle());

        WalkableZone af = new WalkableZone(new LatLng(49.808865, -97.132616), new LatLng(49.808864, -97.132476), new LatLng(49.808636, -97.132573), new LatLng(49.808658, -97.132398));
        af.setTopMiddle(new LatLng(49.808864, -97.132540));
        af.setBottomMiddle(ad.getTopMiddle());

        WalkableZone ag = new WalkableZone(new LatLng(49.809023, -97.132939), new LatLng(49.808922, -97.132469), new LatLng(49.808923, -97.132882), new LatLng(49.808859, -97.132500));
        ag.setLeftMiddle(new LatLng(49.808940, -97.132896));
        ag.setRightMiddle(new LatLng(49.808915, -97.132503));
        ag.setBottomMiddle(af.getTopMiddle());

        WalkableZone ah = new WalkableZone(new LatLng(49.809080, -97.132393), new LatLng(49.809045, -97.132290), new LatLng(49.808922, -97.132550), new LatLng(49.808912, -97.132463));
        ah.setBottomMiddle(ag.getRightMiddle());
        ah.setRightMiddle(q.getLeftMiddle());

        WalkableZone ai = new WalkableZone(new LatLng(49.808272, -97.132776), new LatLng(49.808341, -97.132528), new LatLng(49.808090, -97.132631), new LatLng(49.808176, -97.132409));
        ai.setTopMiddle(new LatLng(49.808329, -97.132564));
        ai.setLeftMiddle(new LatLng(49.808158, -97.132680));
        ai.setRightMiddle(ac.getLeftMiddle());
        ai.setBottomMiddle(new LatLng(49.808165, -97.132435));

        WalkableZone aj = new WalkableZone(new LatLng(49.808544, -97.132795), new LatLng(49.808558, -97.132704), new LatLng(49.808312, -97.132602), new LatLng(49.808337, -97.132518));
        aj.setTopMiddle(eitc_e1_east_ent.getBottomMiddle());
        aj.setBottomMiddle(ai.getTopMiddle());

        WalkableZone ak = new WalkableZone(new LatLng(49.808742, -97.132950), new LatLng(49.808766, -97.132859), new LatLng(49.808554, -97.132812), new LatLng(49.808587, -97.132714));
        ak.setTopMiddle(eitc_e1_e2.getBottomMiddle());
        ak.setBottomMiddle(eitc_e1_east_ent.getTopMiddle());

        WalkableZone al = new WalkableZone(new LatLng(49.808168, -97.132468), new LatLng(49.808182, -97.132423), new LatLng(49.808093, -97.132407), new LatLng(49.808111, -97.132359));
        al.setTopMiddle(ai.getBottomMiddle());
        al.setBottomMiddle(new LatLng(49.808102, -97.132385));

        WalkableZone am = new WalkableZone(new LatLng(49.807741, -97.133363), new LatLng(49.808128, -97.132335), new LatLng(49.807706, -97.133334), new LatLng(49.808083, -97.132316));
        am.setLeftMiddle(dairy_science.getRightMiddle());
        am.setRightMiddle(al.getBottomMiddle());

        WalkableZone an = new WalkableZone(new LatLng(49.807624, -97.133819), new LatLng(49.807731, -97.133399), new LatLng(49.807581, -97.133805), new LatLng(49.807694, -97.133378));
        an.setLeftMiddle(agri_engineer_north_ent.getRightMiddle());
        an.setRightMiddle(dairy_science.getLeftMiddle());

        WalkableZone ao = new WalkableZone(new LatLng(49.807497, -97.134175), new LatLng(49.807590, -97.133892), new LatLng(49.807446, -97.134171), new LatLng(49.807549, -97.133870));
        ao.setLeftMiddle(agri_engineer_south_ent.getRightMiddle());
        ao.setRightMiddle(agri_engineer_north_ent.getLeftMiddle());

        WalkableZone ap = new WalkableZone(new LatLng(49.807198, -97.135103), new LatLng(49.807483, -97.134205), new LatLng(49.807149, -97.135077), new LatLng(49.807446, -97.134198));
        ap.setTopMiddle(new LatLng(49.807312, -97.134700));
        ap.setRightMiddle(agri_engineer_south_ent.getLeftMiddle());
        ap.setLeftMiddle(agriculture_north_ent.getRightMiddle());

        WalkableZone aq = new WalkableZone(new LatLng(49.807395, -97.134866), new LatLng(49.807428, -97.134760), new LatLng(49.807296, -97.134727), new LatLng(49.807322, -97.134668));
        aq.setBottomMiddle(ap.getTopMiddle());
        aq.setTopMiddle(new LatLng(49.807408, -97.134821));

        WalkableZone ar = new WalkableZone(new LatLng(49.807035, -97.135521), new LatLng(49.807170, -97.135176), new LatLng(49.807000, -97.135508), new LatLng(49.807141, -97.135115));
        ar.setLeftMiddle(agriculture_south_ent.getRightMiddle());
        ar.setRightMiddle(agriculture_north_ent.getLeftMiddle());

        WalkableZone as = new WalkableZone(new LatLng(49.806301, -97.137638), new LatLng(49.807022, -97.135584), new LatLng(49.806259, -97.137603), new LatLng(49.806986, -97.135561));
        as.setLeftMiddle(animal_sci_north_ent.getRightMiddle());
        as.setRightMiddle(agriculture_south_ent.getLeftMiddle());

        WalkableZone at = new WalkableZone(new LatLng(49.806143, -97.138145), new LatLng(49.806297, -97.137688), new LatLng(49.806125, -97.138120), new LatLng(49.806243, -97.137678));
        at.setLeftMiddle(animal_sci_south_ent.getRightMiddle());
        at.setRightMiddle(animal_sci_north_ent.getLeftMiddle());

        WalkableZone au = new WalkableZone(new LatLng(49.805983, -97.138623), new LatLng(49.806133, -97.138192), new LatLng(49.805933, -97.138572), new LatLng(49.806089, -97.138197));
        au.setRightMiddle(animal_sci_south_ent.getLeftMiddle());
        au.setTopMiddle(new LatLng(49.805973, -97.138556));

        WalkableZone av = new WalkableZone(new LatLng(49.808749, -97.130731), new LatLng(49.808776, -97.130608), new LatLng(49.808626, -97.130625), new LatLng(49.808666, -97.130509));
        av.setTopMiddle(e.getBottomMiddle());
        av.setBottomMiddle(artlab.getTopMiddle());

        WalkableZone aw = new WalkableZone(new LatLng(49.807856, -97.133648), new LatLng(49.808175, -97.132680), new LatLng(49.807816, -97.133633), new LatLng(49.808144, -97.132659));
        aw.setRightMiddle(ai.getLeftMiddle());
        aw.setLeftMiddle(eitc_e1_e3.getRightMiddle());

        WalkableZone ax = new WalkableZone(new LatLng(49.807225, -97.135525), new LatLng(49.807874, -97.133808), new LatLng(49.807176, -97.135508), new LatLng(49.807779, -97.133750));
        ax.setRightMiddle(eitc_e1_e3.getLeftMiddle());
        ax.setLeftMiddle(new LatLng(49.807202, -97.135511));
        ax.setTopMiddle(russel_south_ent.getBottomMiddle());
        ax.setBottomMiddle(aq.getTopMiddle());

        WalkableZone ay = new WalkableZone(new LatLng(49.807201, -97.135700), new LatLng(49.807252, -97.135570), new LatLng(49.807123, -97.135634), new LatLng(49.807180, -97.135489));
        ay.setRightMiddle(ax.getLeftMiddle());
        ay.setBottomMiddle(new LatLng(49.807156, -97.135650));
        ay.setLeftMiddle(fac_of_music_south_end.getRightMiddle());

        WalkableZone az = new WalkableZone(new LatLng(49.806223, -97.138543), new LatLng(49.807193, -97.135656), new LatLng(49.806115, -97.138502), new LatLng(49.807135, -97.135619));
        az.setLeftMiddle(new LatLng(49.806191, -97.138524));
        az.setRightMiddle(ay.getBottomMiddle());
        az.setTopMiddle(new LatLng(49.807015, -97.136213));

        WalkableZone ba = new WalkableZone(new LatLng(49.806233, -97.138803), new LatLng(49.806306, -97.138689), new LatLng(49.806111, -97.138505), new LatLng(49.806229, -97.138530));
        ba.setRightMiddle(az.getLeftMiddle());
        ba.setLeftMiddle(new LatLng(49.806261, -97.138778));

        WalkableZone bb = new WalkableZone(new LatLng(49.807059, -97.139510), new LatLng(49.807071, -97.139470), new LatLng(49.805933, -97.138566), new LatLng(49.805953, -97.138531));
        bb.setTopMiddle(new LatLng(49.807064, -97.139488));
        bb.setRightMiddle(ba.getLeftMiddle());
        bb.setBottomMiddle(au.getTopMiddle());

        WalkableZone bc = new WalkableZone(new LatLng(49.807073, -97.139467), new LatLng(49.807088, -97.139412), new LatLng(49.807034, -97.139439), new LatLng(49.807053, -97.139378));
        bc.setBottomMiddle(ext_education_west_ent.getTopMiddle());
        bc.setTopMiddle(new LatLng(49.807078, -97.139433));

        WalkableZone bd = new WalkableZone(new LatLng(49.807179, -97.139606), new LatLng(49.807206, -97.139503), new LatLng(49.807056, -97.139504), new LatLng(49.807085, -97.139402));
        bd.setTopMiddle(new LatLng(49.807186, -97.139560));
        bd.setBottomMiddle(bb.getTopMiddle());
        bd.setRightMiddle(bc.getTopMiddle());

        WalkableZone be = new WalkableZone(new LatLng(49.807276, -97.139713), new LatLng(49.807902, -97.137921), new LatLng(49.807171, -97.139606), new LatLng(49.807768, -97.137840));
        be.setRightMiddle(new LatLng(49.807877, -97.137920));
        be.setTopMiddle(new LatLng(49.807288, -97.139629));
        be.setBottomMiddle(bd.getTopMiddle());

        WalkableZone bf = new WalkableZone(new LatLng(49.807283, -97.136411), new LatLng(49.807266, -97.136293), new LatLng(49.807003, -97.136300), new LatLng(49.807039, -97.136176));
        bf.setBottomMiddle(az.getTopMiddle());
        bf.setRightMiddle(new LatLng(49.807283, -97.136321));

        WalkableZone bg = new WalkableZone(new LatLng(49.807359, -97.136397), new LatLng(49.807417, -97.136131), new LatLng(49.807223, -97.136401), new LatLng(49.807338, -97.136022));
        bg.setLeftMiddle(bf.getRightMiddle());
        bg.setRightMiddle(fac_of_music_north_end.getLeftMiddle());

        WalkableZone bh = new WalkableZone(new LatLng(49.807983, -97.138078), new LatLng(49.808087, -97.137764), new LatLng(49.807694, -97.137839), new LatLng(49.807792, -97.137515));
        bh.setLeftMiddle(be.getRightMiddle());
        bh.setRightMiddle(new LatLng(49.807907, -97.137624));
        bh.setBottomMiddle(ext_education_east_ent.getTopMiddle());

        WalkableZone bi = new WalkableZone(new LatLng(49.807986, -97.137703), new LatLng(49.808342, -97.136568), new LatLng(49.807756, -97.137598), new LatLng(49.808160, -97.136410));
        bi.setLeftMiddle(bh.getRightMiddle());
        bi.setRightMiddle(new LatLng(49.808271, -97.136575));
        bi.setTopMiddle(new LatLng(49.808241, -97.136807));
        bi.setBottomMiddle(new LatLng(49.808141, -97.136489));

        WalkableZone bj = new WalkableZone(new LatLng(49.808373, -97.137459), new LatLng(49.808439, -97.137270), new LatLng(49.808218, -97.136837), new LatLng(49.808260, -97.136711));
        bj.setTopMiddle(education_west_ent.getBottomMiddle());
        bj.setBottomMiddle(bi.getTopMiddle());

        WalkableZone bk = new WalkableZone(new LatLng(49.808318, -97.136638), new LatLng(49.808421, -97.136311), new LatLng(49.808214, -97.136558), new LatLng(49.808306, -97.136218));
        bk.setLeftMiddle(bi.getRightMiddle());
        bk.setRightMiddle(new LatLng(49.808361, -97.136284));

        WalkableZone bl = new WalkableZone(new LatLng(49.808146, -97.136589), new LatLng(49.808189, -97.136455), new LatLng(49.808060, -97.136346), new LatLng(49.808091, -97.136224));
        bl.setBottomMiddle(archi2_north_ent.getTopMiddle());
        bl.setTopMiddle(bi.getBottomMiddle());

        WalkableZone bm = new WalkableZone(new LatLng(49.808084, -97.136087), new LatLng(49.808084, -97.135914), new LatLng(49.807750, -97.136240), new LatLng(49.807679, -97.136123));
        bm.setTopMiddle(new LatLng(49.808059, -97.135999));
        bm.setLeftMiddle(archi2_north_ent.getRightMiddle());
        bm.setBottomMiddle(new LatLng(49.807745, -97.136164));

        WalkableZone bn = new WalkableZone(new LatLng(49.807725, -97.136381), new LatLng(49.807799, -97.136184), new LatLng(49.807320, -97.136098), new LatLng(49.807386, -97.135893));
        bn.setTopMiddle(archi2_south_ent.getBottomMiddle());
        bn.setRightMiddle(bm.getBottomMiddle());
        bn.setBottomMiddle(fac_of_music_north_end.getTopMiddle());

        WalkableZone bo = new WalkableZone(new LatLng(49.808272, -97.136020), new LatLng(49.808302, -97.135884), new LatLng(49.808042, -97.136102), new LatLng(49.808033, -97.135948));
        bo.setTopMiddle(new LatLng(49.808279, -97.135949));
        bo.setRightMiddle(new LatLng(49.808183, -97.135918));
        bo.setBottomMiddle(bm.getTopMiddle());

        WalkableZone bp = new WalkableZone(new LatLng(49.808216, -97.135917), new LatLng(49.808318, -97.135581), new LatLng(49.808134, -97.135950), new LatLng(49.808273, -97.135551));
        bp.setLeftMiddle(bo.getRightMiddle());
        bp.setRightMiddle(russel_north_ent.getLeftMiddle());
        bp.setTopMiddle(new LatLng(49.808280, -97.135670));

        WalkableZone bq = new WalkableZone(new LatLng(49.808334, -97.135829), new LatLng(49.808472, -97.135767), new LatLng(49.808264, -97.135693), new LatLng(49.808282, -97.135632));
        bq.setBottomMiddle(bp.getTopMiddle());
        bq.setLeftMiddle(new LatLng(49.808392, -97.135795));
        bq.setRightMiddle(new LatLng(49.808402, -97.135728));

        WalkableZone br = new WalkableZone(new LatLng(49.808429, -97.135922), new LatLng(49.808445, -97.135787), new LatLng(49.808267, -97.136002), new LatLng(49.808294, -97.135882));
        br.setLeftMiddle(new LatLng(49.808306, -97.136012));
        br.setBottomMiddle(bo.getTopMiddle());
        br.setRightMiddle(bq.getLeftMiddle());
        br.setTopMiddle(new LatLng(49.808437, -97.135851));

        WalkableZone bs = new WalkableZone(new LatLng(49.808418, -97.136325), new LatLng(49.808433, -97.136190), new LatLng(49.808267, -97.135978), new LatLng(49.808354, -97.135987));
        bs.setRightMiddle(br.getLeftMiddle());
        bs.setLeftMiddle(bk.getRightMiddle());
        bs.setTopMiddle(new LatLng(49.808419, -97.136262));

        WalkableZone bt = new WalkableZone(new LatLng(49.808641, -97.136126), new LatLng(49.808529, -97.136070), new LatLng(49.808400, -97.136326), new LatLng(49.808419, -97.136191));
        bt.setBottomMiddle(education_south_ent.getBottomMiddle());
        bt.setTopMiddle(bs.getTopMiddle());
        bt.setRightMiddle(new LatLng(49.808568, -97.136101));

        WalkableZone bu = new WalkableZone(new LatLng(49.808591, -97.136154), new LatLng(49.808649, -97.136005), new LatLng(49.808427, -97.135911), new LatLng(49.808446, -97.135763));
        bu.setTopMiddle(new LatLng(49.808605, -97.136014));
        bu.setLeftMiddle(bt.getRightMiddle());
        bu.setBottomMiddle(br.getTopMiddle());

        WalkableZone bv = new WalkableZone(new LatLng(49.808726, -97.136083), new LatLng(49.808729, -97.135940), new LatLng(49.808608, -97.136113), new LatLng(49.808614, -97.135913));
        bv.setTopMiddle(new LatLng(49.808721, -97.136014));
        bv.setBottomMiddle(bu.getTopMiddle());
        bv.setLeftMiddle(education_south_ent.getRightMiddle());

        WalkableZone bw = new WalkableZone(new LatLng(49.808382, -97.135481), new LatLng(49.808445, -97.135284), new LatLng(49.808306, -97.135460), new LatLng(49.808378, -97.135242));
        bw.setTopMiddle(new LatLng(49.808397, -97.135377));
        bw.setLeftMiddle(russel_north_ent.getRightMiddle());

        WalkableZone bx = new WalkableZone(new LatLng(49.808441, -97.135780), new LatLng(49.808541, -97.135535), new LatLng(49.808363, -97.135712), new LatLng(49.808432, -97.135495));
        bx.setLeftMiddle(bq.getRightMiddle());
        bx.setTopMiddle(new LatLng(49.808529, -97.135567));
        bx.setRightMiddle(new LatLng(49.808463, -97.135508));

        WalkableZone by = new WalkableZone(new LatLng(49.808646, -97.135669), new LatLng(49.808644, -97.135568), new LatLng(49.808519, -97.135596), new LatLng(49.808533, -97.135545));
        by.setTopMiddle(helen_glass.getBottomMiddle());
        by.setBottomMiddle(bx.getTopMiddle());

        WalkableZone bz = new WalkableZone(new LatLng(49.808540, -97.135557), new LatLng(49.808670, -97.135079), new LatLng(49.808382, -97.135481), new LatLng(49.808532, -97.134996));
        bz.setLeftMiddle(bx.getRightMiddle());
        bz.setBottomMiddle(bw.getTopMiddle());
        bz.setRightMiddle(new LatLng(49.808617, -97.135022));

        WalkableZone ca = new WalkableZone(new LatLng(49.808745, -97.135150), new LatLng(49.808777, -97.135021), new LatLng(49.808706, -97.135159), new LatLng(49.808745, -97.134995));
        ca.setLeftMiddle(new LatLng(49.808741, -97.135127));
        ca.setBottomMiddle(new LatLng(49.808738, -97.135035));
        ca.setTopMiddle(uni_centre_south_west_ent.getBottomMiddle());

        WalkableZone cb = new WalkableZone(new LatLng(49.808711, -97.135147), new LatLng(49.808784, -97.134894), new LatLng(49.808524, -97.135020), new LatLng(49.808609, -97.134751));
        cb.setTopMiddle(ca.getBottomMiddle());
        cb.setRightMiddle(new LatLng(49.808748, -97.134871));
        cb.setLeftMiddle(bz.getRightMiddle());
        cb.setBottomMiddle(eitc_e3.getLeftMiddle());

        WalkableZone cc = new WalkableZone(new LatLng(49.808747, -97.135500), new LatLng(49.808776, -97.135098), new LatLng(49.808661, -97.135457), new LatLng(49.808711, -97.135082));
        cc.setLeftMiddle(helen_glass.getRightMiddle());
        cc.setRightMiddle(ca.getLeftMiddle());

        WalkableZone cd = new WalkableZone(new LatLng(49.808883, -97.135066), new LatLng(49.808994, -97.134727), new LatLng(49.808696, -97.134909), new LatLng(49.808811, -97.134602));
        cd.setLeftMiddle(cb.getRightMiddle());
        cd.setTopMiddle(uni_centre_west_ent.getBottomMiddle());
        cd.setRightMiddle(new LatLng(49.808953, -97.134693));

        WalkableZone ce = new WalkableZone(new LatLng(49.809002, -97.134745), new LatLng(49.809182, -97.134225), new LatLng(49.808867, -97.134635), new LatLng(49.809019, -97.134121));
        ce.setLeftMiddle(cd.getRightMiddle());
        ce.setRightMiddle(new LatLng(49.809129, -97.134215));

        WalkableZone cf = new WalkableZone(new LatLng(49.809198, -97.134243), new LatLng(49.809346, -97.133747), new LatLng(49.808991, -97.134100), new LatLng(49.809162, -97.133537));
        cf.setLeftMiddle(ce.getRightMiddle());
        cf.setBottomMiddle(new LatLng(49.809141, -97.133597));
        cf.setTopMiddle(uni_centre_north_ent.getBottomMiddle());

        WalkableZone cg = new WalkableZone(new LatLng(49.809155, -97.133702), new LatLng(49.809196, -97.133509), new LatLng(49.808985, -97.133496), new LatLng(49.809029, -97.133355));
        cg.setTopMiddle(cf.getBottomMiddle());
        cg.setBottomMiddle(eitc_e2.getTopMiddle());
        cg.setLeftMiddle(eitc_e3.getRightMiddle());
        cg.setRightMiddle(new LatLng(49.809058, -97.133384));

        WalkableZone ch = new WalkableZone(new LatLng(49.809004, -97.133166), new LatLng(49.809034, -97.133082), new LatLng(49.808760, -97.132960), new LatLng(49.808783, -97.132879));
        ch.setTopMiddle(new LatLng(49.809016, -97.133126));
        ch.setRightMiddle(new LatLng(49.808905, -97.132985));
        ch.setBottomMiddle(eitc_e1_e2.getTopMiddle());

        WalkableZone ci = new WalkableZone(new LatLng(49.808929, -97.133014), new LatLng(49.808965, -97.132892), new LatLng(49.808883, -97.132978), new LatLng(49.808931, -97.132850));
        ci.setRightMiddle(ag.getLeftMiddle());
        ci.setLeftMiddle(ch.getRightMiddle());

        WalkableZone cj = new WalkableZone(new LatLng(49.809029, -97.133360), new LatLng(49.809084, -97.133116), new LatLng(49.808952, -97.133304), new LatLng(49.809022, -97.133072));
        cj.setBottomMiddle(ch.getTopMiddle());
        cj.setLeftMiddle(eitc_e2.getRightMiddle());
        cj.setTopMiddle(new LatLng(49.809077, -97.133165));

        WalkableZone ck = new WalkableZone(new LatLng(49.809077, -97.133430), new LatLng(49.809193, -97.133043), new LatLng(49.809012, -97.133397), new LatLng(49.809138, -97.132937));
        ck.setRightMiddle(p.getLeftMiddle());
        ck.setBottomMiddle(cj.getTopMiddle());
        ck.setLeftMiddle(cg.getRightMiddle());
        ck.setTopMiddle(new LatLng(49.809142, -97.133212));

        WalkableZone cl = new WalkableZone(new LatLng(49.809464, -97.133621), new LatLng(49.809518, -97.133424), new LatLng(49.809102, -97.133344), new LatLng(49.809161, -97.133147));
        cl.setTopMiddle(new LatLng(49.809507, -97.133477));
        cl.setLeftMiddle(uni_centre_east_ent.getRightMiddle());
        cl.setBottomMiddle(ck.getTopMiddle());

        WalkableZone cm = new WalkableZone(new LatLng(49.809806, -97.133977), new LatLng(49.809876, -97.133627), new LatLng(49.809452, -97.133705), new LatLng(49.809536, -97.133379));
        cm.setTopMiddle(new LatLng(49.809848, -97.133752));
        cm.setRightMiddle(new LatLng(49.809859, -97.133622));
        cm.setLeftMiddle(uni_centre_north_ent.getRightMiddle());
        cm.setBottomMiddle(cl.getTopMiddle());

        WalkableZone cn = new WalkableZone(new LatLng(49.810146, -97.134445), new LatLng(49.810267, -97.133978), new LatLng(49.809733, -97.134127), new LatLng(49.809862, -97.133667));
        cn.setTopMiddle(new LatLng(49.810234, -97.134121));
        cn.setRightMiddle(new LatLng(49.810139, -97.133881));
        cn.setBottomMiddle(cm.getTopMiddle());

        WalkableZone co = new WalkableZone(new LatLng(49.810164, -97.133919), new LatLng(49.810314, -97.133476), new LatLng(49.810104, -97.133870), new LatLng(49.810245, -97.133437));
        co.setLeftMiddle(cn.getRightMiddle());
        co.setTopMiddle(buller_west_ent.getBottomMiddle());
        co.setRightMiddle(new LatLng(49.810274, -97.133466));
        co.setBottomMiddle(new LatLng(49.810238, -97.133482));

        WalkableZone cp = new WalkableZone(new LatLng(49.810300, -97.133507), new LatLng(49.810420, -97.133157), new LatLng(49.810233, -97.133467), new LatLng(49.810351, -97.133110));
        cp.setLeftMiddle(co.getRightMiddle());
        cp.setRightMiddle(new LatLng(49.810384, -97.133138));
        cp.setBottomMiddle(new LatLng(49.810311, -97.133283));

        WalkableZone cq = new WalkableZone(new LatLng(49.810416, -97.133178), new LatLng(49.810547, -97.132773), new LatLng(49.810361, -97.133132), new LatLng(49.810472, -97.132723));
        cq.setTopMiddle(buller_east_ent.getBottomMiddle());
        cq.setBottomMiddle(new LatLng(49.810374, -97.133095));
        cq.setLeftMiddle(cp.getRightMiddle());
        cq.setRightMiddle(new LatLng(49.810506, -97.132747));

        WalkableZone cr = new WalkableZone(new LatLng(49.809860, -97.133681), new LatLng(49.810209, -97.132661), new LatLng(49.809831, -97.133646), new LatLng(49.810174, -97.132609));
        cr.setLeftMiddle(cn.getRightMiddle());
        cr.setTopMiddle(new LatLng(49.809900, -97.133574));
        cr.setRightMiddle(new LatLng(49.810189, -97.132649));

        WalkableZone cs = new WalkableZone(new LatLng(49.810230, -97.133519), new LatLng(49.810254, -97.133439), new LatLng(49.810190, -97.133489), new LatLng(49.810211, -97.133359));
        cs.setTopMiddle(co.getBottomMiddle());
        cs.setRightMiddle(new LatLng(49.810233, -97.133400));
        cs.setBottomMiddle(new LatLng(49.810191, -97.133440));

        WalkableZone ct = new WalkableZone(new LatLng(49.810190, -97.133490), new LatLng(49.810199, -97.133379), new LatLng(49.809867, -97.133646), new LatLng(49.809918, -97.133500));
        ct.setTopMiddle(cs.getBottomMiddle());
        ct.setBottomMiddle(cr.getTopMiddle());

        WalkableZone cu = new WalkableZone(new LatLng(49.810239, -97.133472), new LatLng(49.810369, -97.133123), new LatLng(49.810181, -97.133393), new LatLng(49.810296, -97.133055));
        cu.setLeftMiddle(cs.getRightMiddle());
        cu.setTopMiddle(cp.getBottomMiddle());
        cu.setRightMiddle(new LatLng(49.810328, -97.133116));

        WalkableZone cv = new WalkableZone(new LatLng(49.810372, -97.133132), new LatLng(49.810390, -97.133056), new LatLng(49.810287, -97.133081), new LatLng(49.810298, -97.132996));
        cv.setTopMiddle(cq.getBottomMiddle());
        cv.setLeftMiddle(cu.getRightMiddle());
        cv.setBottomMiddle(new LatLng(49.810318, -97.133030));

        WalkableZone cw = new WalkableZone(new LatLng(49.810295, -97.133077), new LatLng(49.810353, -97.133018), new LatLng(49.810168, -97.132644), new LatLng(49.810196, -97.132495));
        cw.setTopMiddle(cv.getBottomMiddle());
        cw.setLeftMiddle(cr.getRightMiddle());
        cw.setRightMiddle(new LatLng(49.810230, -97.132519));

        WalkableZone cx = new WalkableZone(new LatLng(49.810374, -97.132618), new LatLng(49.810438, -97.132403), new LatLng(49.809984, -97.132287), new LatLng(49.810045, -97.132122));
        cx.setTopMiddle(new LatLng(49.810384, -97.132566));
        cx.setLeftMiddle(cw.getRightMiddle());
        cx.setRightMiddle(new LatLng(49.810396, -97.132415));
        cx.setBottomMiddle(eli_dafoe.getTopMiddle());

        WalkableZone cy = new WalkableZone(new LatLng(49.810627, -97.132859), new LatLng(49.810664, -97.132730), new LatLng(49.810369, -97.132637), new LatLng(49.810394, -97.132510));
        cy.setBottomMiddle(cx.getTopMiddle());
        cy.setTopMiddle(new LatLng(49.810641, -97.132802));
        cy.setLeftMiddle(cq.getRightMiddle());

        WalkableZone cz = new WalkableZone(new LatLng(49.810747, -97.132954), new LatLng(49.810804, -97.132787), new LatLng(49.810617, -97.132860), new LatLng(49.810673, -97.132680));
        cz.setTopMiddle(duff_roblin_west_ent.getBottomMiddle());
        cz.setRightMiddle(duff_roblin_south_ent.getLeftMiddle());
        cz.setBottomMiddle(cy.getTopMiddle());

        WalkableZone da = new WalkableZone(new LatLng(49.811059, -97.133230), new LatLng(49.811084, -97.133163), new LatLng(49.810963, -97.133124), new LatLng(49.810982, -97.133066));
        da.setLeftMiddle(armes_machray.getRightMiddle());
        da.setBottomMiddle(duff_roblin_west_ent.getTopMiddle());

        WalkableZone db = new WalkableZone(new LatLng(49.810425, -97.132439), new LatLng(49.810901, -97.131183), new LatLng(49.810387, -97.132391), new LatLng(49.810801, -97.131093));
        db.setLeftMiddle(cx.getRightMiddle());
        db.setRightMiddle(uni_college.getLeftMiddle());
        db.setTopMiddle(human_eco.getBottomMiddle());

        WalkableZone dc = new WalkableZone(new LatLng(49.810884, -97.132470), new LatLng(49.810902, -97.132392), new LatLng(49.810793, -97.132486), new LatLng(49.810808, -97.132425));
        dc.setTopMiddle(duff_roblin_south_ent.getBottomMiddle());
        dc.setBottomMiddle(human_eco.getTopMiddle());

        WalkableZone dd = new WalkableZone(new LatLng(49.810362, -97.134388), new LatLng(49.810430, -97.134156), new LatLng(49.810188, -97.134258), new LatLng(49.810251, -97.134033));
        dd.setTopMiddle(new LatLng(49.810405, -97.134235));
        dd.setLeftMiddle(bio_science_east_ent.getRightMiddle());
        dd.setBottomMiddle(cn.getTopMiddle());

        WalkableZone de = new WalkableZone(new LatLng(49.810467, -97.134375), new LatLng(49.810512, -97.134234), new LatLng(49.810373, -97.134297), new LatLng(49.810410, -97.134163));
        de.setTopMiddle(allen_armes.getBottomMiddle());
        de.setBottomMiddle(dd.getTopMiddle());

        WalkableZone df = new WalkableZone(new LatLng(49.810479, -97.134623), new LatLng(49.810507, -97.134537), new LatLng(49.810315, -97.134509), new LatLng(49.810336, -97.134419));
        df.setLeftMiddle(new LatLng(49.810412, -97.134578));
        df.setRightMiddle(allen_armes.getLeftMiddle());
        df.setBottomMiddle(bio_science_east_ent.getTopMiddle());

        WalkableZone dg = new WalkableZone(new LatLng(49.810242, -97.135169), new LatLng(49.810480, -97.134612), new LatLng(49.810183, -97.135117), new LatLng(49.810377, -97.134508));
        dg.setRightMiddle(df.getLeftMiddle());
        dg.setLeftMiddle(new LatLng(49.810209, -97.135130));

        WalkableZone dh = new WalkableZone(new LatLng(49.810204, -97.135301), new LatLng(49.810247, -97.135146), new LatLng(49.810047, -97.135304), new LatLng(49.810107, -97.135056));
        dh.setLeftMiddle(new LatLng(49.810131, -97.135299));
        dh.setBottomMiddle(bio_science_west_ent.getTopMiddle());
        dh.setRightMiddle(dg.getLeftMiddle());

        WalkableZone di = new WalkableZone(new LatLng(49.810175, -97.135767), new LatLng(49.810229, -97.135289), new LatLng(49.810049, -97.135663), new LatLng(49.810111, -97.135292));
        di.setRightMiddle(dh.getLeftMiddle());
        di.setTopMiddle(new LatLng(49.810206, -97.135508));
        di.setLeftMiddle(new LatLng(49.810080, -97.135680));

        WalkableZone dj = new WalkableZone(new LatLng(49.809937, -97.136187), new LatLng(49.810107, -97.135687), new LatLng(49.809887, -97.136155), new LatLng(49.810053, -97.135647));
        dj.setLeftMiddle(robert_schultz_south_ent.getRightMiddle());
        dj.setRightMiddle(di.getLeftMiddle());

        WalkableZone dk = new WalkableZone(new LatLng(49.810359, -97.135407), new LatLng(49.810391, -97.135261), new LatLng(49.810181, -97.135569), new LatLng(49.810184, -97.135413));
        dk.setBottomMiddle(di.getTopMiddle());
        dk.setTopMiddle(new LatLng(49.810354, -97.135384));

        WalkableZone dl = new WalkableZone(new LatLng(49.811212, -97.136105), new LatLng(49.811227, -97.136027), new LatLng(49.810356, -97.135395), new LatLng(49.810381, -97.135279));
        dl.setTopMiddle(new LatLng(49.811221, -97.136078));
        dl.setBottomMiddle(dk.getTopMiddle());
        dl.setRightMiddle(new LatLng(49.810686, -97.135648));

        WalkableZone dm = new WalkableZone(new LatLng(49.811294, -97.136101), new LatLng(49.811348, -97.135804), new LatLng(49.811261, -97.136075), new LatLng(49.811328, -97.135794));
        dm.setTopMiddle(new LatLng(49.811341, -97.135821));
        dm.setRightMiddle(parker.getLeftMiddle());
        dm.setLeftMiddle(new LatLng(49.811282, -97.136095));

        WalkableZone dn = new WalkableZone(new LatLng(49.811406, -97.136256), new LatLng(49.811409, -97.136198), new LatLng(49.811229, -97.136118), new LatLng(49.811245, -97.136068));
        dn.setTopMiddle(new LatLng(49.811414, -97.136219));
        dn.setBottomMiddle(dl.getTopMiddle());
        dn.setRightMiddle(dm.getLeftMiddle());

        WalkableZone dp = new WalkableZone(new LatLng(49.810697, -97.135739), new LatLng(49.810972, -97.134640), new LatLng(49.810647, -97.135700), new LatLng(49.810934, -97.134603));
        dp.setLeftMiddle(dl.getRightMiddle());
        dp.setRightMiddle(allen_armes_parker.getLeftMiddle());

        WalkableZone dq = new WalkableZone(new LatLng(49.811258, -97.136906), new LatLng(49.811545, -97.135899), new LatLng(49.811228, -97.136883), new LatLng(49.811510, -97.135889));
        dq.setRightMiddle(new LatLng(49.811528, -97.135902));
        dq.setBottomMiddle(dn.getTopMiddle());
        dq.setLeftMiddle(st_johns_east_ent.getRightMiddle());

        WalkableZone dr = new WalkableZone(new LatLng(49.811572, -97.135897), new LatLng(49.811566, -97.135773), new LatLng(49.811325, -97.135885), new LatLng(49.811348, -97.135793));
        dr.setBottomMiddle(dm.getTopMiddle());
        dr.setTopMiddle(new LatLng(49.811570, -97.135836));
        dr.setLeftMiddle(dq.getRightMiddle());

        WalkableZone ds = new WalkableZone(new LatLng(49.811623, -97.135949), new LatLng(49.811559, -97.135283), new LatLng(49.811545, -97.135899), new LatLng(49.811501, -97.135308));
        ds.setBottomMiddle(dr.getTopMiddle());
        ds.setTopMiddle(wallace.getBottomMiddle());
        ds.setRightMiddle(new LatLng(49.811553, -97.135272));

        WalkableZone dt = new WalkableZone(new LatLng(49.811662, -97.135257), new LatLng(49.811695, -97.135034), new LatLng(49.811542, -97.135286), new LatLng(49.811622, -97.135047));
        dt.setLeftMiddle(ds.getRightMiddle());
        dt.setBottomMiddle(new LatLng(49.811579, -97.135171));

        WalkableZone du = new WalkableZone(new LatLng(49.811571, -97.135219), new LatLng(49.811598, -97.135135), new LatLng(49.811502, -97.135153), new LatLng(49.811530, -97.135078));
        du.setTopMiddle(dt.getBottomMiddle());
        du.setBottomMiddle(parker.getTopMiddle());

        WalkableZone dv = new WalkableZone(new LatLng(49.810962, -97.137900), new LatLng(49.811269, -97.136978), new LatLng(49.810900, -97.137855), new LatLng(49.811218, -97.136928));
        dv.setTopMiddle(new LatLng(49.811036, -97.137669));
        dv.setLeftMiddle(new LatLng(49.810917, -97.137856));
        dv.setRightMiddle(st_johns_east_ent.getLeftMiddle());
        dv.setBottomMiddle(new LatLng(49.810979, -97.137597));

        WalkableZone dw = new WalkableZone(new LatLng(49.810963, -97.137684), new LatLng(49.811016, -97.137524), new LatLng(49.810754, -97.137487), new LatLng(49.810787, -97.137403));
        dw.setLeftMiddle(new LatLng(49.810832, -97.137564));
        dw.setTopMiddle(dv.getBottomMiddle());
        dw.setBottomMiddle(new LatLng(49.810791, -97.137452));

        WalkableZone dx = new WalkableZone(new LatLng(49.810923, -97.137877), new LatLng(49.810853, -97.137518), new LatLng(49.810885, -97.137982), new LatLng(49.810807, -97.137499));
        dx.setLeftMiddle(new LatLng(49.810871, -97.137991));
        dx.setRightMiddle(dv.getLeftMiddle());
        dx.setBottomMiddle(dw.getLeftMiddle());

        WalkableZone dy = new WalkableZone(new LatLng(49.810344, -97.139603), new LatLng(49.810908, -97.137951), new LatLng(49.810305, -97.139586), new LatLng(49.810870, -97.137942));
        dy.setRightMiddle(dx.getLeftMiddle());
        dy.setBottomMiddle(new LatLng(49.810719, -97.138378));

        WalkableZone dz = new WalkableZone(new LatLng(49.810721, -97.138424), new LatLng(49.810745, -97.138348), new LatLng(49.810514, -97.138274), new LatLng(49.810547, -97.138193));
        dz.setTopMiddle(dy.getBottomMiddle());
        dz.setBottomMiddle(st_pauls_north_ent.getTopMiddle());

        WalkableZone ea = new WalkableZone(new LatLng(49.810786, -97.137523), new LatLng(49.810853, -97.137223), new LatLng(49.810591, -97.137406), new LatLng(49.810668, -97.137168));
        ea.setBottomMiddle(new LatLng(49.810609, -97.137357));
        ea.setTopMiddle(dw.getBottomMiddle());
        ea.setRightMiddle(st_johns_north_ent.getLeftMiddle());

        WalkableZone eb = new WalkableZone(new LatLng(49.810564, -97.137383), new LatLng(49.810610, -97.137415), new LatLng(49.810502, -97.137196), new LatLng(49.810536, -97.137151));
        eb.setTopMiddle(ea.getBottomMiddle());
        eb.setBottomMiddle(new LatLng(49.810545, -97.137339));
        eb.setRightMiddle(st_johns_west_ent.getTopMiddle());

        WalkableZone ec = new WalkableZone(new LatLng(49.810583, -97.137403), new LatLng(49.810545, -97.137309), new LatLng(49.810087, -97.137195), new LatLng(49.810107, -97.137138));
        ec.setTopMiddle(eb.getBottomMiddle());
        ec.setRightMiddle(st_johns_west_ent.getLeftMiddle());
        ec.setBottomMiddle(new LatLng(49.810125, -97.137179));

        WalkableZone ed = new WalkableZone(new LatLng(49.810120, -97.137207), new LatLng(49.810128, -97.136973), new LatLng(49.810029, -97.137103), new LatLng(49.810041, -97.136920));
        ed.setTopMiddle(ec.getBottomMiddle());
        ed.setRightMiddle(robert_schultz_west_ent.getLeftMiddle());
        ed.setLeftMiddle(st_pauls_east_ent.getRightMiddle());
        ed.setBottomMiddle(new LatLng(49.810038, -97.136942));

        WalkableZone ee = new WalkableZone(new LatLng(49.810056, -97.137020), new LatLng(49.810078, -97.136918), new LatLng(49.809698, -97.136707), new LatLng(49.809721, -97.136650));
        ee.setTopMiddle(ed.getBottomMiddle());
        ee.setRightMiddle(new LatLng(49.809749, -97.136681));
        ee.setLeftMiddle(new LatLng(49.809737, -97.136764));

        WalkableZone ef = new WalkableZone(new LatLng(49.809753, -97.136762), new LatLng(49.809855, -97.136444), new LatLng(49.809717, -97.136721), new LatLng(49.809819, -97.136405));
        ef.setRightMiddle(robert_schultz_south_ent.getLeftMiddle());
        ef.setLeftMiddle(ee.getRightMiddle());

        WalkableZone eg = new WalkableZone(new LatLng(49.809907, -97.137265), new LatLng(49.809937, -97.137201), new LatLng(49.809742, -97.136922), new LatLng(49.809768, -97.136879));
        eg.setTopMiddle(st_pauls_east_ent.getBottomMiddle());
        eg.setBottomMiddle(new LatLng(49.809763, -97.136915));

        WalkableZone eh = new WalkableZone(new LatLng(49.809753, -97.136946), new LatLng(49.809762, -97.136760), new LatLng(49.809242, -97.136546), new LatLng(49.809298, -97.136395));
        eh.setTopMiddle(eg.getBottomMiddle());
        eh.setRightMiddle(ee.getLeftMiddle());
        eh.setBottomMiddle(new LatLng(49.809265, -97.136474));

        WalkableZone ei = new WalkableZone(new LatLng(49.809267, -97.136529), new LatLng(49.809297, -97.136386), new LatLng(49.808813, -97.136140), new LatLng(49.808852, -97.136002));
        ei.setTopMiddle(eh.getBottomMiddle());
        ei.setLeftMiddle(education_north_ent.getRightMiddle());
        ei.setBottomMiddle(new LatLng(49.808828, -97.136078));

        WalkableZone ej = new WalkableZone(new LatLng(49.808794, -97.136136), new LatLng(49.808865, -97.135979), new LatLng(49.808712, -97.136087), new LatLng(49.808772, -97.135775));
        ej.setTopMiddle(ei.getBottomMiddle());
        ej.setBottomMiddle(bv.getTopMiddle());
        ej.setRightMiddle(helen_glass.getLeftMiddle());

        // = new WalkableZone(new LatLng(), new LatLng(), new LatLng(), new LatLng());
    }
}
