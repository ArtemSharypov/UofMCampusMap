package com.artem.uofmcampusmap;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-04-28.
 */

public class MapGraph {
    private HashMap<String, ArrayList<Vertex>> buildingEntrances;
    private Context context;

    public MapGraph()
    {
        buildingEntrances = new HashMap<>();
        populateGraph();
    }

    private void addVertex(String keyName, ArrayList<Vertex> entranceVertices)
    {
        if(buildingEntrances.containsKey(keyName))
        {
            entranceVertices.addAll(buildingEntrances.get(keyName));
        }

        buildingEntrances.put(keyName, entranceVertices);
    }

    private void populateGraph()
    {
        //todo: fill the graph with the necessary entrances & routes overall
        //Entrances
        String agriculture = context.getResources().getResourceName(R.string.agriculture);
        Vertex agriculture_ent_AAQ = new Vertex(agriculture, new LatLng(49.806958, -97.135498));
        Vertex agriculture_ent_AAP = new Vertex(agriculture, new LatLng(49.807100, -97.135065));

        String agri_engineer = context.getResources().getResourceName(R.string.agr_engineer);
        Vertex agri_engineer_ent_A36 = new Vertex(agri_engineer, new LatLng(49.807397, -97.134141));
        Vertex agri_engineer_ent_A37 = new Vertex(agri_engineer, new LatLng(49.807542, -97.133847));

        String animal_sci = context.getResources().getResourceName(R.string.animal_sci);
        Vertex animal_sci_ent_A25 = new Vertex(animal_sci, new LatLng(49.806032, -97.138085));
        Vertex animal_sci_ent_AAR = new Vertex(animal_sci, new LatLng(49.806214, -97.137599));

        String architecture2 = context.getResources().getResourceName(R.string.archi_2);
        Vertex archi2_ent_AAI = new Vertex(architecture2, new LatLng(49.807786, -97.136334));
        Vertex archi2_ent_A32 = new Vertex(architecture2, new LatLng(49.807970, -97.136214));

        String armes = context.getResources().getResourceName(R.string.armes);
        String parker = context.getResources().getResourceName(R.string.parker);
        String allen = context.getResources().getResourceName(R.string.allen);
        Vertex armes_parker_allen_ent_K = new Vertex(armes + " " + parker + " " + allen, new LatLng(49.810988, -97.134381));
        Vertex armes_ent_Q = new Vertex(armes, new LatLng(49.810596, -97.134033));

        String artlab = context.getResources().getResourceName(R.string.art_lab);
        Vertex artlab_ent_A40 = new Vertex(artlab, new LatLng(49.808630, -97.130618));

        String bio_sci = context.getResources().getResourceName(R.string.bio_sci);
        Vertex bio_sci_ent_P = new Vertex(bio_sci, new LatLng(49.810098, -97.135059));
        Vertex bio_sci_ent_R = new Vertex(bio_sci, new LatLng(49.810287, -97.134455));

        String buller = context.getResources().getResourceName(R.string.buller);
        Vertex buller_ent_U1 = new Vertex(buller, new LatLng(49.810321, -97.133550));
        Vertex buller_ent_U2 = new Vertex(buller, new LatLng(49.810448, -97.133160));

        String dairy_sci = context.getResources().getResourceName(R.string.dairy_science);
        Vertex dairy_sci_ent_A38 = new Vertex(dairy_sci, new LatLng(49.807654, -97.133320));

        String drake = context.getResources().getResourceName(R.string.drake_centre);
        Vertex drake_ent_A39 = new Vertex(drake, new LatLng(49.808220, -97.130219));

        String duff_roblin = context.getResources().getResourceName(R.string.duff_roblin);
        Vertex duff_roblin_ent_A42 = new Vertex(duff_roblin, new LatLng(49.811223, -97.132487));
        Vertex duff_roblin_ent_A43 = new Vertex(duff_roblin, new LatLng(49.810894, -97.132999));
        Vertex duff_roblin_ent_AB = new Vertex(duff_roblin, new LatLng(49.810961, -97.132287));

        String education = context.getResources().getResourceName(R.string.education);
        Vertex education_ent_AAB = new Vertex(education, new LatLng(49.808759, -97.136257));
        Vertex education_ent_AAC = new Vertex(education, new LatLng(49.808621, -97.136411));
        Vertex education_ent_A33 = new Vertex(education, new LatLng(49.808453, -97.137288));
        Vertex education_ent_A45 = new Vertex(education, new LatLng(49.809137, -97.136573));

        String elizabeth_dafoe = context.getResources().getResourceName(R.string.elizabeth_dafoe);
        Vertex eli_dafoe_lib_ent_Y_AE = new Vertex(elizabeth_dafoe, new LatLng(49.809936, -97.131678));

        String engineer1 = context.getResources().getResourceName(R.string.eitc_e1);
        String engineer2 = context.getResources().getResourceName(R.string.eitc_e2);
        String engineer3 = context.getResources().getResourceName(R.string.eitc_e3);
        Vertex e1_e2_ent_AV = new Vertex(engineer1 + " " + engineer2, new LatLng(49.808242, -97.134007));
        Vertex engineer1_entAQ = new Vertex(engineer1, new LatLng(49.808455, -97.133076));
        Vertex engineer2_entAP = new Vertex(engineer2, new LatLng(49.808647, -97.133253));
        Vertex engineer2_entA31 = new Vertex(engineer2, new LatLng(49.808954, -97.133421));
        Vertex engineer3_entAU = new Vertex(engineer3, new LatLng(49.808679, -97.134501));

        String ext_educ = context.getResources().getResourceName(R.string.ext_education);
        Vertex ext_educ_ent_A25 = new Vertex(ext_educ, new LatLng(49.807552, -97.137838));
        Vertex ext_educ_ent_A26 = new Vertex(ext_educ, new LatLng(49.807048, -97.139307));

        String faculty_music = context.getResources().getResourceName(R.string.fac_music);
        Vertex fac_music_ent_AAI = new Vertex(faculty_music, new LatLng(49.807374, -97.135999));
        Vertex fac_music_ent_AAN = new Vertex(faculty_music, new LatLng(49.807183, -97.135739));

        String fletcher = context.getResources().getResourceName(R.string.fletcher);
        Vertex fletcher_ent_AH = new Vertex(fletcher, new LatLng(49.809564, -97.131417));

        String helen_glass = context.getResources().getResourceName(R.string.helen_glass);
        Vertex helen_glass_ent_A41 = new Vertex(helen_glass, new LatLng(49.808810, -97.135583));

        String human_eco = context.getResources().getResourceName(R.string.human_ecology);
        Vertex human_eco_ent_A44 = new Vertex(human_eco, new LatLng(49.810706, -97.132340));

        String istbister = context.getResources().getResourceName(R.string.isbister);
        Vertex istbister_ent_AK_A28 = new Vertex(istbister, new LatLng(49.809108, -97.130241));

        String machray = context.getResources().getResourceName(R.string.machray);
        Vertex machray_armes_ent_A28 = new Vertex(machray + " " + armes, new LatLng(49.810997, -97.133403));

        Vertex parker_ent_A35 = new Vertex(parker, new LatLng(49.811579, -97.134812));

        String robert_schultz = context.getResources().getResourceName(R.string.robert_schultz);
        Vertex rob_schutlz_ent_A14 = new Vertex(robert_schultz, new LatLng(49.809953, -97.136343));

        String robson = context.getResources().getResourceName(R.string.robson);
        Vertex robson_ent_AD = new Vertex(robson, new LatLng(49.811697, -97.130995));

        String russel = context.getResources().getResourceName(R.string.russel);
        Vertex russel_ent_AW = new Vertex(russel, new LatLng(49.807901, -97.135211));
        Vertex russel_ent_AY = new Vertex(russel, new LatLng(49.808198, -97.135456));

        String st_johns = context.getResources().getResourceName(R.string.st_johns);
        Vertex st_john_ent_C  = new Vertex(st_johns, new LatLng(49.810899, -97.136611));
        Vertex st_john_ent_B = new Vertex(st_johns, new LatLng(49.810748, -97.137068));
        Vertex st_john_ent_24_23 = new Vertex(st_johns, new LatLng(49.810549, -97.137036));
        Vertex rob_schult_st_johns_ent_22_19 = new Vertex(st_johns, new LatLng(49.810205, -97.136741));

        String st_pauls = context.getResources().getResourceName(R.string.st_pauls);
        Vertex st_pauls_ent_A = new Vertex(st_pauls, new LatLng(49.810458, -97.138114));
        Vertex st_pauls_ent_A16 = new Vertex(st_pauls, new LatLng(49.809881, -97.137395));

        String tier = context.getResources().getResourceName(R.string.tier);
        Vertex tier_ent_AJ = new Vertex(tier, new LatLng(49.808939, -97.130748));
        Vertex tier_ent_A29 = new Vertex(tier, new LatLng(49.808866, -97.130578)); //mainly for tunnels / lower floor so not important ish

        String uni_college = context.getResources().getResourceName(R.string.uni_college);
        Vertex uni_college_ent_AC = new Vertex(uni_college, new LatLng(49.811055, -97.131105));

        String uni_centre = context.getResources().getResourceName(R.string.uni_centre);
        Vertex uni_centre_ent_T = new Vertex(uni_centre, new LatLng(49.809416, -97.133864));
        Vertex uni_centre_ent_AM = new Vertex(uni_centre, new LatLng(49.809290, -97.133710));
        Vertex uni_centre_ent_A29 = new Vertex(uni_centre, new LatLng(49.809058, -97.135026));

        String wallace = context.getResources().getResourceName(R.string.wallace);
        Vertex wallace_ent = new Vertex(wallace, new LatLng(49.811639, -97.135697));

        //Other points
        Vertex E_F_G;
        Vertex E_D;
        Vertex D_C;
        Vertex D_B;
        Vertex D_A;
        Vertex B_A21;
        Vertex A24_A23;
        Vertex A24_A21;
        Vertex A23_A21;
        Vertex A23_A22;
        Vertex A22_A19;
        Vertex A21_A20;
        Vertex A20_A18_A19_A17;
        Vertex A18_A16_A15;
        Vertex A15_A17_M;
        Vertex A14_M;
        Vertex F_H;
        Vertex G_I;
        Vertex I_H;
        Vertex A35_I;
        Vertex J_H;
        Vertex J_K;
        Vertex H_L;
        Vertex L_M;
        Vertex L_P;
        Vertex L_Q;
        Vertex Q_S;
        Vertex R_S;
        Vertex S_U;
        Vertex U_AAU;
        Vertex U_AAW;
        Vertex U_W;
        Vertex A28_W;
        Vertex V_W;
        Vertex V_AA;
        Vertex V_AB;
        Vertex W_A43;
        Vertex A28_A42;
        Vertex A44_AA;
        Vertex W_Z;
        Vertex AA_Z;
        Vertex Z_AC;
        Vertex AD_Z;
        Vertex AAT_AAW;
        Vertex AAT_W;
        Vertex AAT_AAU;
        Vertex AAT_S;
        Vertex T_S;
        Vertex AM_S;
        Vertex Y_W;
        Vertex AE_W;
        Vertex Y_AE_AF;
        Vertex AF_MID;
        Vertex MID_AG;
        Vertex AG_AH;
        Vertex AH_W;
        Vertex AI_W;
        Vertex AL_W;
        Vertex AK_AL;
        Vertex A28_AL;
        Vertex AJ_AL;
        Vertex A29_AL;
        Vertex AAV_W;
        Vertex AAV_S;
        Vertex AAV_A9;
        Vertex W_AAZ;
        Vertex W_A10_AL;
        Vertex W_A30;
        Vertex AAZ_A1_A2;
        Vertex A30_A2_A3;
        Vertex A3_A4_A10;
        Vertex A4_A5_A11;
        Vertex A6_A5_A13;
        Vertex A5_A12;
        Vertex A6_A7;
        Vertex A7_A8_AAY;
        Vertex A8_A1_A9;
        Vertex A11_AL;
        Vertex A12_AL;
        Vertex A13_AL;
        Vertex AAY_S;
        Vertex S_AL;
        Vertex AQ_S;
        Vertex AP_S;
        Vertex AO_S;
        Vertex AN_S;
        Vertex AO_A31;
        Vertex AN_AS;
        Vertex AS_AR_AT;
        Vertex AR_AO;
        Vertex AO_AU;
        Vertex AO_AAM;
        Vertex AN_A30;
        Vertex A30_A29;
        Vertex AN_AAA_AAS;
        Vertex AT_AAS;
        Vertex M_O;
        Vertex M_N;
        Vertex O_A45;
        Vertex O_AAB;
        Vertex O_AAA_AAC_LP6;
        Vertex LP6_LP5_LP1;
        Vertex LP5_LP4;
        Vertex LP4_AAK_LP3;
        Vertex LP3_LP2_AAF;
        Vertex LP1_LP2;
        Vertex AAM_AAK;
        Vertex AAM_AAJ;
        Vertex AAJ_AY;
        Vertex AAJ_AAH;
        Vertex AAH_AAG;
        Vertex AAH_AAI;
        Vertex AAO_AAN;
        Vertex AL_AAN;
        Vertex AL_AAO;
        Vertex AV_AL;
        Vertex AW_AL;
        Vertex AAG_AAE_AAF;
        Vertex A32_AAG;
        Vertex AAE_AAD;
        Vertex AAD_A33;
        Vertex N_AAF;
        Vertex N_A25;
        Vertex AAF_A34;
        Vertex A27_AAF;
        Vertex A27_A26;
        Vertex AL_A27;
        Vertex A27_AL2;
        Vertex A25_AL2;
        Vertex AAR_AL2;
        Vertex AAQ_AL2;
        Vertex AAP_AL2;
        Vertex A36_AL2;
        Vertex A37_AL2;
        Vertex A38_AL2;
        Vertex A39_AL2;
        Vertex A40_A39;
        Vertex A41_AN;
    }

    private void connectVertices(Vertex vertex1, Vertex vertex2)
    {
        Edge v1ToV2 = new Edge(vertex1, vertex2);
        vertex1.addConnection(v1ToV2);

        Edge v2ToV1 = new Edge(vertex2, vertex1);
        vertex2.addConnection(v2ToV1);
    }

    public Route findRoute(String startLocation, String endLocation)
    {
        Route route = null;
        Route prevRoute;
        ArrayList<Vertex> entrances;
        ArrayList<Vertex> exits;
        RouteFinder routeFinder = new RouteFinder();

        if(buildingEntrances.containsKey(startLocation) && buildingEntrances.containsKey(endLocation))
        {
            entrances = buildingEntrances.get(startLocation);
            exits = buildingEntrances.get(endLocation);

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
}
