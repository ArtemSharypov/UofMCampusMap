package com.artem.uofmcampusmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-04-28.
 */

public class MapGraph {
    private HashMap<String, ArrayList<Vertex>> buildingEntrances;

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
        Vertex agriculture_ent_AAQ;
        Vertex agriculture_ent_AAP;
        Vertex agri_engineer_ent_A36;
        Vertex agri_engineer_ent_A37;
        Vertex animal_sci_ent_A25;
        Vertex animal_sci_ent_AAR;
        Vertex archi2_ent_AAI;
        Vertex archi2_ent_A32;
        Vertex armes_parker_allen_ent_K;
        Vertex armes_ent_Q;
        Vertex artlab_ent_A40;
        Vertex bio_sci_ent_P;
        Vertex bio_sci_ent_R;
        Vertex buller_ent_U1;
        Vertex buller_ent_U2;
        Vertex dairy_sci_ent_A38;
        Vertex drake_ent_A39;
        Vertex education_ent_AAB;
        Vertex education_ent_AAC;
        Vertex education_ent_A33;
        Vertex eli_dafoe_lib_ent_Y_AE;
        Vertex e1_e2_ent_AV;
        Vertex engineer1_entAQ;
        Vertex engineer2_entAP;
        Vertex engineer2_entA31;
        Vertex engineer3_entAU;
        Vertex ext_educ_ent_A34;
        Vertex ext_educ_ent_A25;
        Vertex ext_educ_ent_A26;
        Vertex fac_music_ent_AAI;
        Vertex fac_music_ent_AAN;
        Vertex fletcher_ent_AH;
        Vertex helen_glass_ent_A41;
        Vertex istbister_ent_AK_A28;
        Vertex machray_armes_ent_A28;
        Vertex parker_ent_A35;
        Vertex rob_schutlz_ent_A14;
        Vertex robson_ent_AD;
        Vertex russel_ent_AW;
        Vertex russel_ent_AY;
        Vertex st_john_ent_C;
        Vertex st_john_ent_B;
        Vertex st_john_ent_24_23;
        Vertex st_johns_ent_22_19;
        Vertex st_pauls_ent_A;
        Vertex st_pauls_ent_A16;
        Vertex tier_ent_AJ;
        Vertex tier_ent_A29; //mainly for tunnels / lower floor so not important ish
        Vertex uni_college_ent_AC;
        Vertex uni_centre_ent_T;
        Vertex uni_centre_ent_AM;
        Vertex uni_centre_ent_A29;
        Vertex wallace_ent;

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
