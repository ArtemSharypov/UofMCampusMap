package com.artem.uofmcampusmap;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-04-28.
 */

public class MapGraph {
    private HashMap<String, ArrayList<Vertex>> startEndLocations;

    public MapGraph()
    {
        startEndLocations = new HashMap<>();
        populateGraph();
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

    private void connectVertices(Vertex vertex1, Vertex vertex2)
    {
        vertex1.addConnection(vertex2);
        vertex2.addConnection(vertex1);
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

    private void populateGraph()
    {
        //todo: find a way to easily use the strings from resources without the extra package infos
        //Entrances
        String agriculture = "Agriculture";
        Vertex agriculture_ent_AAQ = new Vertex(agriculture, new LatLng(49.806958, -97.135498));
        Vertex agriculture_ent_AAP = new Vertex(agriculture, new LatLng(49.807100, -97.135065));

        String agri_engineer = "Agricultural Engineering";
        Vertex agri_engineer_ent_A36 = new Vertex(agri_engineer, new LatLng(49.807406, -97.134132));
        Vertex agri_engineer_ent_A37 = new Vertex(agri_engineer, new LatLng(49.807491, -97.133790));

        String animal_sci = "Animal Science / Entomology";
        Vertex animal_sci_ent_A25 = new Vertex(animal_sci, new LatLng(49.806032, -97.138085));
        Vertex animal_sci_ent_AAR = new Vertex(animal_sci, new LatLng(49.806214, -97.137599));

        String architecture2 = "Architecture 2";
        Vertex archi2_ent_AAI = new Vertex(architecture2, new LatLng(49.807786, -97.136334));
        Vertex archi2_ent_A32 = new Vertex(architecture2, new LatLng(49.807970, -97.136214));

        String armes = "Armes";
        String parker = "Parker";
        String allen = "Allen";
        Vertex armes_parker_allen_ent_K = new Vertex(armes + " " + parker + " " + allen, new LatLng(49.810988, -97.134381));
        Vertex armes_ent_Q = new Vertex(armes, new LatLng(49.810596, -97.134033));

        String artlab = "Artlab";
        Vertex artlab_ent_A40 = new Vertex(artlab, new LatLng(49.808648, -97.130393));

        String bio_sci = "Biological Science";
        Vertex bio_sci_ent_P = new Vertex(bio_sci, new LatLng(49.810090, -97.135044));
        Vertex bio_sci_ent_R = new Vertex(bio_sci, new LatLng(49.810287, -97.134455));

        String buller = "Buller";
        Vertex buller_ent_U1 = new Vertex(buller, new LatLng(49.810321, -97.133550));
        Vertex buller_ent_U2 = new Vertex(buller, new LatLng(49.810448, -97.133160));

        String dairy_sci = "Dairy Science";
        Vertex dairy_sci_ent_A38 = new Vertex(dairy_sci, new LatLng(49.807654, -97.133320));

        String drake = "Drake Centre";
        Vertex drake_ent_A39 = new Vertex(drake, new LatLng(49.808220, -97.130219));

        String duff_roblin = "Duff Roblin";
        Vertex duff_roblin_ent_A42 = new Vertex(duff_roblin, new LatLng(49.811223, -97.132487));
        Vertex duff_roblin_ent_A43 = new Vertex(duff_roblin, new LatLng(49.810894, -97.132999));
        Vertex duff_roblin_ent_AB = new Vertex(duff_roblin, new LatLng(49.810961, -97.132287));

        String education = "Education";
        Vertex education_ent_AAB = new Vertex(education, new LatLng(49.808759, -97.136257));
        Vertex education_ent_AAC = new Vertex(education, new LatLng(49.808621, -97.136411));
        Vertex education_ent_A33 = new Vertex(education, new LatLng(49.808453, -97.137288));
        Vertex education_ent_A57 = new Vertex(education, new LatLng(49.809137, -97.136573));

        String elizabeth_dafoe = "Elizabeth Dafoe";
        Vertex eli_dafoe_lib_ent_Y_AE = new Vertex(elizabeth_dafoe, new LatLng(49.809936, -97.131678));

        String eitc1 = "EITC E1";
        String eitc2 = "EITC E2";
        String eitc3 = "EITC E3";
        Vertex eitc1_eitc3_ent_AV = new Vertex(eitc1 + " " + eitc2, new LatLng(49.808242, -97.134007));
        Vertex eitc1_ent_AQ = new Vertex(eitc1, new LatLng(49.808455, -97.133076));
        Vertex eitc2_ent_AP = new Vertex(eitc2, new LatLng(49.808647, -97.133253));
        Vertex eitc2_ent_A31 = new Vertex(eitc2, new LatLng(49.808954, -97.133421));
        Vertex eitc3_ent_AU = new Vertex(eitc3, new LatLng(49.808679, -97.134501));

        String ext_educ = "Extended Education";
        Vertex ext_educ_ent_A58 = new Vertex(ext_educ, new LatLng(49.807550, -97.137832));
        Vertex ext_educ_ent_A26 = new Vertex(ext_educ, new LatLng(49.807048, -97.139307));

        String faculty_music = "Faculty of Music";
        Vertex fac_music_ent_AAI = new Vertex(faculty_music, new LatLng(49.807374, -97.135999));
        Vertex fac_music_ent_AAN = new Vertex(faculty_music, new LatLng(49.807183, -97.135739));

        String fletcher = "Fletcher";
        Vertex fletcher_ent_AH = new Vertex(fletcher, new LatLng(49.809564, -97.131417));

        String helen_glass = "Helen Glass";
        Vertex helen_glass_ent_A41 = new Vertex(helen_glass, new LatLng(49.808810, -97.135583));

        String human_eco = "Human Ecology";
        Vertex human_eco_ent_A44 = new Vertex(human_eco, new LatLng(49.810706, -97.132340));

        String istbister = "Isbister";
        Vertex istbister_ent_AK_A60 = new Vertex(istbister, new LatLng(49.809108, -97.130241));

        String machray = "Machray";
        Vertex machray_armes_ent_A28 = new Vertex(machray + " " + armes, new LatLng(49.810997, -97.133403));

        Vertex parker_ent_A35 = new Vertex(parker, new LatLng(49.811579, -97.134812));

        String robert_schultz = "Robert Schultz Theatre";
        Vertex rob_schutlz_ent_A14 = new Vertex(robert_schultz, new LatLng(49.809953, -97.136343));

        String robson = "Robson";
        Vertex robson_ent_AD = new Vertex(robson, new LatLng(49.811751, -97.131005));

        String russel = "Russel";
        Vertex russel_ent_AW = new Vertex(russel, new LatLng(49.807901, -97.135211));
        Vertex russel_ent_AY = new Vertex(russel, new LatLng(49.808198, -97.135456));

        String st_johns = "St.Johns College";
        Vertex st_john_ent_C  = new Vertex(st_johns, new LatLng(49.810899, -97.136611));
        Vertex st_john_ent_A46 = new Vertex(st_johns, new LatLng(49.810748, -97.137068));
        Vertex st_john_ent_A24_A23 = new Vertex(st_johns, new LatLng(49.810549, -97.137036));
        Vertex rob_schult_st_johns_ent_A22_A19 = new Vertex(st_johns, new LatLng(49.810200, -97.136742));

        String st_pauls = "St.Pauls College";
        Vertex st_pauls_ent_A = new Vertex(st_pauls, new LatLng(49.810458, -97.138114));
        Vertex st_pauls_ent_A16 = new Vertex(st_pauls, new LatLng(49.809881, -97.137395));

        String tier = "Tier";
        Vertex tier_ent_AJ = new Vertex(tier, new LatLng(49.808939, -97.130748));
        Vertex tier_ent_A59 = new Vertex(tier, new LatLng(49.808866, -97.130578)); //mainly for tunnels / lower floor so not important ish
        Vertex tier_ent_AI = new Vertex(tier, new LatLng(49.809175, -97.131135));

        String uni_college = "University College";
        Vertex uni_college_ent_AC = new Vertex(uni_college, new LatLng(49.811055, -97.131105));

        String uni_centre = "University Centre";
        Vertex uni_centre_ent_T = new Vertex(uni_centre, new LatLng(49.809453, -97.133924));
        Vertex uni_centre_ent_AM = new Vertex(uni_centre, new LatLng(49.809290, -97.133710));
        Vertex uni_centre_ent_A29 = new Vertex(uni_centre, new LatLng(49.809058, -97.135026));
        Vertex uni_centre_ent_AS = new Vertex(uni_centre, new LatLng(49.808777, -97.135054));

        String wallace = "Wallace";
        Vertex wallace_ent = new Vertex(wallace, new LatLng(49.811639, -97.135697));

        //Points used for paths
        //Starts with single
        Vertex A_D = new Vertex(new LatLng(49.810733, -97.138385));
        Vertex B_D = new Vertex(new LatLng(49.810992, -97.137633));
        Vertex B_A21_A45 = new Vertex(new LatLng(49.810656, -97.137366));
        Vertex C_D = new Vertex(new LatLng(49.811250, -97.136879));
        Vertex D_E_H = new Vertex(new LatLng(49.811423, -97.136240));
        Vertex E_F = new Vertex(new LatLng(49.811589, -97.135828));
        Vertex E_F_G = new Vertex(new LatLng(49.811588, -97.135712));
        Vertex F_I = new Vertex(new LatLng(49.811333, -97.135826));
        Vertex G_I = new Vertex(new LatLng(49.811510, -97.135103));
        Vertex H_I = new Vertex(new LatLng(49.811285, -97.136112));
        Vertex H_J = new Vertex(new LatLng(49.810654, -97.135593));
        Vertex H_L = new Vertex(new LatLng(49.810354, -97.135364));
        Vertex I_A35 = new Vertex(new LatLng(49.811592, -97.134816));
        Vertex J_K = new Vertex(new LatLng(49.810955, -97.134620));
        Vertex L_M = new Vertex(new LatLng(49.810113, -97.135576));
        Vertex L_A56 = new Vertex(new LatLng(49.810160, -97.135529));
        Vertex M_O = new Vertex(new LatLng(49.809709, -97.136797));
        Vertex M_A14 = new Vertex(new LatLng(49.809884, -97.136281));
        Vertex M_A15 = new Vertex(new LatLng(49.809707, -97.136842));
        Vertex M_A17 = new Vertex(new LatLng(49.809745, -97.136707));
        Vertex N_AAF = new Vertex(new LatLng(49.807898, -97.137776));
        Vertex N_A58 = new Vertex(new LatLng(49.807579, -97.137750));
        Vertex O_AAA_AAC_LP6 = new Vertex(new LatLng(49.808720, -97.136015));
        Vertex O_AAB = new Vertex(new LatLng(49.808819, -97.136071));
        Vertex Q_A47 = new Vertex(new LatLng(49.810205, -97.135152));
        Vertex Q_A56 = new Vertex(new LatLng(49.810134, -97.135267));
        Vertex O_A57 = new Vertex(new LatLng(49.809201, -97.136399));
        Vertex P_A47 = new Vertex(new LatLng(49.810086, -97.135066));
        Vertex Q_S = new Vertex(new LatLng(49.810502, -97.134322));
        Vertex R_S = new Vertex(new LatLng(49.810356, -97.134215));
        Vertex S_U = new Vertex(new LatLng(49.810097, -97.134001));
        Vertex S_AL = new Vertex(new LatLng(49.808202, -97.132459));
        Vertex S_AM = new Vertex(new LatLng(49.809366, -97.133421));
        Vertex S_AO_AAV = new Vertex(new LatLng(49.809091, -97.133163));
        Vertex S_AP = new Vertex(new LatLng(49.808764, -97.132921));
        Vertex S_AQ = new Vertex(new LatLng(49.808559, -97.132751));
        Vertex S_AAT = new Vertex(new LatLng(49.809821, -97.133766));
        Vertex S_AL2 = new Vertex(new LatLng(49.808085, -97.132369));
        Vertex S_A50 = new Vertex(new LatLng(49.809618, -97.133597));
        Vertex S_A53 = new Vertex(new LatLng(49.808894, -97.133020));
        Vertex T_A50_A51 = new Vertex(new LatLng(49.809489, -97.133801));
        Vertex U_W = new Vertex(new LatLng(49.810539, -97.132721));
        Vertex U_AAU = new Vertex(new LatLng(49.810191, -97.133445));
        Vertex U_AAW = new Vertex(new LatLng(49.810324, -97.133047));
        Vertex U_U1 = new Vertex(new LatLng(49.810265, -97.133501));
        Vertex U_U2 = new Vertex(new LatLng(49.810395, -97.133117));
        Vertex V_W = new Vertex(new LatLng(49.810752, -97.132914));
        Vertex V_AB = new Vertex(new LatLng(49.810954, -97.132282));
        Vertex V_A48 = new Vertex(new LatLng(49.810784, -97.132799));
        Vertex V_A49 = new Vertex(new LatLng(49.810899, -97.132447));
        Vertex W_Z = new Vertex(new LatLng(49.810361, -97.132558));
        Vertex W_AE = new Vertex(new LatLng(49.809734, -97.131940));
        Vertex W_AH = new Vertex(new LatLng(49.809470, -97.131720));
        Vertex W_AI = new Vertex(new LatLng(49.809117, -97.131423));
        Vertex W_AL_A10 = new Vertex(new LatLng(49.808644, -97.131080));
        Vertex W_AAZ_A52 = new Vertex(new LatLng(49.809397, -97.131698));
        Vertex W_AAT = new Vertex(new LatLng(49.810253, -97.132450));
        Vertex W_A28 = new Vertex(new LatLng(49.811049, -97.133148));
        Vertex W_A30 = new Vertex(new LatLng(49.809030, -97.131369));
        Vertex W_A43 = new Vertex(new LatLng(49.810870, -97.133014));
        Vertex W_A48 = new Vertex(new LatLng(49.810688, -97.132843));
        Vertex W_Y = new Vertex(new LatLng(49.809994, -97.132167));
        Vertex Y_AE_AF = new Vertex(new LatLng(49.809901, -97.131709));
        Vertex Z_AA = new Vertex(new LatLng(49.810406, -97.132402));
        Vertex Z_AC = new Vertex(new LatLng(49.810919, -97.130977));
        Vertex Z_AD = new Vertex(new LatLng(49.811158, -97.130407));

        //Starts with double
        Vertex AA_A44 = new Vertex(new LatLng(49.810692, -97.132372));
        Vertex AA_A49 = new Vertex(new LatLng(49.810814, -97.132467));
        Vertex AF_A55 = new Vertex(new LatLng(49.809799, -97.131503));
        Vertex AG_AH = new Vertex(new LatLng(49.809546, -97.131433));
        Vertex AG_A54 = new Vertex(new LatLng(49.809604, -97.131482));
        Vertex AJ_AL = new Vertex(new LatLng(49.808791, -97.130653));
        Vertex AK_AL = new Vertex(new LatLng(49.808964, -97.130162));
        Vertex AL_AW = new Vertex(new LatLng(49.807426, -97.134807));
        Vertex AL_AV = new Vertex(new LatLng(49.807799, -97.133741));
        Vertex AL_AAN = new Vertex(new LatLng(49.807194, -97.135541));
        Vertex AL_A11 = new Vertex(new LatLng(49.808419, -97.131737));
        Vertex AL_A12 = new Vertex(new LatLng(49.808385, -97.131824));
        Vertex AL_A13 = new Vertex(new LatLng(49.808248, -97.132312));
        Vertex AL_A27 = new Vertex(new LatLng(49.806183, -97.138606));
        Vertex AL_A59 = new Vertex(new LatLng(49.808822, -97.130542));
        Vertex AL_A60 = new Vertex(new LatLng(49.808844, -97.130504));
        Vertex AN_A30 = new Vertex(new LatLng(49.808888, -97.134933));
        Vertex AN_A51 = new Vertex(new LatLng(49.809261, -97.133832));
        Vertex AO_AS = new Vertex(new LatLng(49.808601, -97.134933));
        Vertex AO_AU = new Vertex(new LatLng(49.808763, -97.134453));
        Vertex AO_AAM = new Vertex(new LatLng(49.808427, -97.135449));
        Vertex AO_A31 = new Vertex(new LatLng(49.809024, -97.133488));
        Vertex AR_AS = new Vertex(new LatLng(49.808707, -97.135006));
        Vertex AR_A30 = new Vertex(new LatLng(49.808771, -97.134822));
        Vertex AS_AT = new Vertex(new LatLng(49.808753, -97.135040));
        Vertex AT_AAA_AAM_A41 = new Vertex(new LatLng(49.808734, -97.135606));
        Vertex AW_AL2 = new Vertex(new LatLng(49.807294, -97.134684));
        Vertex AY_AAJ = new Vertex(new LatLng(49.808297, -97.135545));
        Vertex A1_A8_A9 = new Vertex(new LatLng(49.809073, -97.132298));
        Vertex A2_A3_A30 = new Vertex(new LatLng(49.808936, -97.131622));
        Vertex A3_A4_A10 = new Vertex(new LatLng(49.808703, -97.131580));
        Vertex A4_A5_A11 = new Vertex(new LatLng(49.808567, -97.131712));
        Vertex A5_A6_A13 = new Vertex(new LatLng(49.808480, -97.132224));
        Vertex A5_A12 = new Vertex(new LatLng(49.808476, -97.132063));
        Vertex A6_A7 = new Vertex(new LatLng(49.808655, -97.132539));
        Vertex A15_A16_A18 = new Vertex(new LatLng(49.809929, -97.137234));
        Vertex A17_A18_A19_A20 = new Vertex(new LatLng(49.810037, -97.137051));
        Vertex A19_A22 = new Vertex(new LatLng(49.810204, -97.136786));
        Vertex A20_A21 = new Vertex(new LatLng(49.810132, -97.137196));
        Vertex A21_A24 = new Vertex(new LatLng(49.810568, -97.137337));
        Vertex A21_A23 = new Vertex(new LatLng(49.810435, -97.137310));
        Vertex A22_A23_A24 = new Vertex(new LatLng(49.810511, -97.137170));
        Vertex A28_A42 = new Vertex(new LatLng(49.811276, -97.132537));
        Vertex A45_A46 = new Vertex(new LatLng(49.810794, -97.137122));
        Vertex A54_A55 = new Vertex(new LatLng(49.809686, -97.131429));

        //Starts with triple
        Vertex AAD_AAE = new Vertex(new LatLng(49.808364, -97.137251));
        Vertex AAD_A33 = new Vertex(new LatLng(49.808431, -97.137319));
        Vertex AAE_AAF = new Vertex(new LatLng(49.808224, -97.136760));
        Vertex AAF_AAG = new Vertex(new LatLng(49.808238, -97.136637));
        Vertex AAF_A27 = new Vertex(new LatLng(49.807250, -97.139614));
        Vertex AAF_LP2_LP3 = new Vertex(new LatLng(49.808369, -97.136263));
        Vertex AAG_AAH = new Vertex(new LatLng(49.808004, -97.136044));
        Vertex AAG_A32 = new Vertex(new LatLng(49.808053, -97.136251)); //todo connect this point to others for quicker route
        Vertex AAH_AAJ = new Vertex(new LatLng(49.808173, -97.135956));
        Vertex AAH_AAI = new Vertex(new LatLng(49.807683, -97.136191));
        Vertex AAJ_AAM = new Vertex(new LatLng(49.808351, -97.135394));
        Vertex AAK_AAM = new Vertex(new LatLng(49.808485, -97.135491));
        Vertex AAK_LP3_LP4 = new Vertex(new LatLng(49.808323, -97.135943));
        Vertex AAT_AAW = new Vertex(new LatLng(49.810221, -97.132566));
        Vertex AAT_AAU = new Vertex(new LatLng(49.809871, -97.133596));
        Vertex AAV_A52 = new Vertex(new LatLng(49.809436, -97.132023));
        Vertex AAV_A9 = new Vertex(new LatLng(49.809283, -97.132460));
        Vertex AAY_A7_A8 = new Vertex(new LatLng(49.808895, -97.132538));
        Vertex AAY_A53 = new Vertex(new LatLng(49.808967, -97.132845));
        Vertex AAZ_A1_A2 = new Vertex(new LatLng(49.809086, -97.131898));
        Vertex AL2_AAP = new Vertex(new LatLng(49.807151, -97.135103));
        Vertex AL2_AAQ = new Vertex(new LatLng(49.807007, -97.135533));
        Vertex AL2_AAR = new Vertex(new LatLng(49.806269, -97.137642));
        Vertex AL2_A25 = new Vertex(new LatLng(49.806084, -97.138131));
        Vertex AL2_A27 = new Vertex(new LatLng(49.805955, -97.138535));
        Vertex AL2_A36 = new Vertex(new LatLng(49.807465, -97.134170));
        Vertex AL2_A37 = new Vertex(new LatLng(49.807552, -97.133820));
        Vertex AL2_A38 = new Vertex(new LatLng(49.807714, -97.133368));
        Vertex AL2_A39 = new Vertex(new LatLng(49.808686, -97.130599));
        Vertex A26_A27 = new Vertex(new LatLng(49.807031, -97.139409));
        Vertex A29_A30 = new Vertex(new LatLng(49.809041, -97.135083));
        Vertex A39_A40 = new Vertex(new LatLng(49.808638, -97.130480));
        Vertex LP1_LP2 = new Vertex(new LatLng(49.808490, -97.136255));
        Vertex LP1_LP5_LP6 = new Vertex(new LatLng(49.808573, -97.136018));
        Vertex LP4_LP5 = new Vertex(new LatLng(49.808448, -97.135856));

        //connections start with initial single letter vertex
        connectVertices(A_D, B_D);
        connectVertices(A_D, C_D);
        connectVertices(A_D, D_E_H);
        connectVertices(B_D, D_E_H);
        connectVertices(B_D, B_A21_A45);
        connectVertices(B_D, C_D);
        connectVertices(B_A21_A45, A21_A24);
        connectVertices(B_A21_A45, A45_A46);
        connectVertices(B_A21_A45, A21_A23);
        connectVertices(B_A21_A45, A20_A21);
        connectVertices(C_D, D_E_H);
        connectVertices(D_E_H, E_F);
        connectVertices(D_E_H, H_I);
        connectVertices(D_E_H, H_J);
        connectVertices(D_E_H, H_L);
        connectVertices(E_F, E_F_G);
        connectVertices(E_F, F_I);
        connectVertices(E_F_G, G_I);
        connectVertices(F_I, H_I);
        connectVertices(F_I, I_A35);
        connectVertices(G_I, F_I);
        connectVertices(G_I, I_A35);
        connectVertices(H_I, H_J);
        connectVertices(H_I, G_I);
        connectVertices(H_I, I_A35);
        connectVertices(H_I, H_L);
        connectVertices(H_J, J_K);
        connectVertices(H_J, H_L);
        connectVertices(H_L, L_A56);
        connectVertices(H_L, L_M);
        connectVertices(L_M, L_A56);
        connectVertices(L_M, M_A14);
        connectVertices(L_M, M_A15);
        connectVertices(L_M, M_A17);
        connectVertices(L_M, M_O);
        connectVertices(L_A56, Q_A56);
        connectVertices(M_O, M_A15);
        connectVertices(M_O, M_A14);
        connectVertices(M_O, M_A17);
        connectVertices(M_O, O_A57);
        connectVertices(M_O, O_AAB);
        connectVertices(M_O, O_AAA_AAC_LP6);
        connectVertices(M_A14, M_A17);
        connectVertices(M_A14, M_A15);
        connectVertices(M_A15, M_A17);
        connectVertices(M_A15, A15_A16_A18);
        connectVertices(M_A17, A17_A18_A19_A20);
        connectVertices(N_AAF, AAF_AAG);
        connectVertices(N_AAF, AAF_LP2_LP3);
        connectVertices(N_AAF, N_A58);
        connectVertices(N_AAF, AAE_AAF);
        connectVertices(N_AAF, AAF_A27);
        connectVertices(O_AAA_AAC_LP6, O_AAB);
        connectVertices(O_AAA_AAC_LP6, AT_AAA_AAM_A41);
        connectVertices(O_AAA_AAC_LP6, LP1_LP5_LP6);
        connectVertices(O_AAA_AAC_LP6, O_A57);
        connectVertices(O_AAB, O_A57);
        connectVertices(P_A47, Q_A47);
        connectVertices(Q_S, R_S);
        connectVertices(Q_S, Q_A47);
        connectVertices(Q_S, S_U);
        connectVertices(Q_S, S_AAT);
        connectVertices(Q_S, S_A50);
        connectVertices(Q_S, S_AM);
        connectVertices(Q_S, S_AO_AAV);
        connectVertices(Q_S, S_AP);
        connectVertices(Q_S, S_AQ);
        connectVertices(Q_S, S_AL);
        connectVertices(Q_S, S_AL2);
        connectVertices(Q_A47, Q_A56);
        connectVertices(Q_A47, Q_S);
        connectVertices(Q_A47, armes_ent_Q);
        connectVertices(Q_A56, Q_S);
        connectVertices(Q_A56, armes_ent_Q);
        connectVertices(R_S, S_U);
        connectVertices(R_S, S_AAT);
        connectVertices(R_S, S_A50);
        connectVertices(R_S, S_AM);
        connectVertices(R_S, S_AO_AAV);
        connectVertices(R_S, S_AP);
        connectVertices(R_S, S_AQ);
        connectVertices(R_S, S_AL);
        connectVertices(R_S, S_AL2);
        connectVertices(S_U, U_U2);
        connectVertices(S_U, U_W);
        connectVertices(S_U, S_AAT);
        connectVertices(S_U, U_U1);
        connectVertices(S_U, S_A50);
        connectVertices(S_U, S_AM);
        connectVertices(S_U, S_AO_AAV);
        connectVertices(S_U, S_AP);
        connectVertices(S_U, S_AQ);
        connectVertices(S_U, S_AL);
        connectVertices(S_U, S_AL2);
        connectVertices(S_AL, AL_AV);
        connectVertices(S_AL, S_AQ);
        connectVertices(S_AL, S_AL2);
        connectVertices(S_AL, AL_A13);
        connectVertices(S_AL, AL_A11);
        connectVertices(S_AL, AL_A12);
        connectVertices(S_AL, W_AL_A10);
        connectVertices(S_AL, AJ_AL);
        connectVertices(S_AL, AL_A59);
        connectVertices(S_AL, AL_A60);
        connectVertices(S_AL, AK_AL);
        connectVertices(S_AL, AL_AV);
        connectVertices(S_AL, AL_AW);
        connectVertices(S_AL, AL_AAN);
        connectVertices(S_AL, AL_A27);
        connectVertices(S_AL, S_AAT);
        connectVertices(S_AM, S_AO_AAV);
        connectVertices(S_AM, S_A50);
        connectVertices(S_AM, S_A50);
        connectVertices(S_AM, S_AO_AAV);
        connectVertices(S_AM, S_A53);
        connectVertices(S_AM, S_AP);
        connectVertices(S_AM, S_AQ);
        connectVertices(S_AM, S_AL);
        connectVertices(S_AM, S_AL2);
        connectVertices(S_AM, S_AAT);
        connectVertices(S_AO_AAV, S_A53);
        connectVertices(S_AO_AAV, AO_A31);
        connectVertices(S_AO_AAV, AAV_A9);
        connectVertices(S_AO_AAV, S_A50);
        connectVertices(S_AO_AAV, S_AP);
        connectVertices(S_AO_AAV, S_AQ);
        connectVertices(S_AO_AAV, S_AL);
        connectVertices(S_AO_AAV, S_AL2);
        connectVertices(S_AO_AAV, S_AAT);
        connectVertices(S_AO_AAV, AAV_A52);
        connectVertices(S_AO_AAV, AO_AU);
        connectVertices(S_AO_AAV, AO_AS);
        connectVertices(S_AO_AAV, AO_AAM);
        connectVertices(S_AO_AAV, AAV_A9);
        connectVertices(S_AP, S_AQ);
        connectVertices(S_AP, S_A53);
        connectVertices(S_AP, S_AL);
        connectVertices(S_AP, S_AL2);
        connectVertices(S_AP, S_A50);
        connectVertices(S_AP, S_AAT);
        connectVertices(S_AQ, S_AL);
        connectVertices(S_AQ, S_AL2);
        connectVertices(S_AQ, S_A50);
        connectVertices(S_AQ, S_A53);
        connectVertices(S_AQ, S_AAT);
        connectVertices(S_AAT, S_A50);
        connectVertices(S_AAT, AAT_AAU);
        connectVertices(S_AAT, AAT_AAW);
        connectVertices(S_AAT, W_AAT);
        connectVertices(S_AAT, S_A50);
        connectVertices(S_AAT, S_AL2);
        connectVertices(S_AAT, S_AL);
        connectVertices(S_AL2, AL2_A38);
        connectVertices(S_AL2, AL2_A39);
        connectVertices(S_AL2, AL2_A27);
        connectVertices(S_AL2, AL2_A25);
        connectVertices(S_AL2, AL2_AAR);
        connectVertices(S_AL2, AL2_AAQ);
        connectVertices(S_AL2, AL2_AAP);
        connectVertices(S_AL2, AW_AL2);
        connectVertices(S_AL2, AL2_A36);
        connectVertices(S_AL2, AL2_A37);
        connectVertices(S_A50, T_A50_A51);
        connectVertices(S_A50, S_AL);
        connectVertices(S_A50, S_AL2);
        connectVertices(S_A53, AAY_A53);
        connectVertices(T_A50_A51, AN_A51);
        connectVertices(U_W, W_AAZ_A52);
        connectVertices(U_W, W_A43);
        connectVertices(U_W, W_A28);
        connectVertices(U_W, W_AAT);
        connectVertices(U_W, W_Y);
        connectVertices(U_W, W_AE);
        connectVertices(U_W, W_AH);
        connectVertices(U_W, W_AI);
        connectVertices(U_W, W_A30);
        connectVertices(U_W, W_AL_A10);
        connectVertices(U_W, V_W);
        connectVertices(U_W, W_Z);
        connectVertices(U_W, U_U2);
        connectVertices(U_W, W_A48);
        connectVertices(U_W, U_U1);
        connectVertices(U_U1, U_U2);
        connectVertices(U_AAU, U_U1);
        connectVertices(U_AAU, AAT_AAU);
        connectVertices(U_AAW, U_U2);
        connectVertices(U_AAW, AAT_AAW);
        connectVertices(U_AAU, U_AAW);
        connectVertices(V_W, W_AAZ_A52);
        connectVertices(V_W, V_A49);
        connectVertices(V_W, V_AB);
        connectVertices(V_W, V_A48);
        connectVertices(V_W, W_A43);
        connectVertices(V_W, W_A48);
        connectVertices(V_W, W_A28);
        connectVertices(V_W, W_Z);
        connectVertices(V_W, W_AAT);
        connectVertices(V_W, W_Y);
        connectVertices(V_W, W_AE);
        connectVertices(V_W, W_AH);
        connectVertices(V_W, W_AI);
        connectVertices(V_W, W_A30);
        connectVertices(V_W, W_AL_A10);
        connectVertices(V_AB, V_A49);
        connectVertices(V_AB, V_A48);
        connectVertices(V_A48, V_A49);
        connectVertices(V_A48, W_A48);
        connectVertices(V_A49, AA_A49);
        connectVertices(W_Y, W_AAZ_A52);
        connectVertices(W_Y, Y_AE_AF);
        connectVertices(W_Y, AF_A55);
        connectVertices(W_Y, W_AAT);
        connectVertices(W_Y, W_Z);
        connectVertices(W_Y, W_A28);
        connectVertices(W_Y, W_A43);
        connectVertices(W_Y, W_A48);
        connectVertices(W_Y, W_AE);
        connectVertices(W_Y, W_AH);
        connectVertices(W_Y, W_AI);
        connectVertices(W_Y, W_A30);
        connectVertices(W_Y, W_AL_A10);
        connectVertices(W_Z, W_AAZ_A52);
        connectVertices(W_Z, W_AAT);
        connectVertices(W_Z, Z_AA);
        connectVertices(W_Z, Z_AC);
        connectVertices(W_Z, Z_AD);
        connectVertices(W_Z, W_A48);
        connectVertices(W_Z, W_A28);
        connectVertices(W_Z, W_A43);
        connectVertices(W_Z, W_AE);
        connectVertices(W_Z, W_AH);
        connectVertices(W_Z, W_AI);
        connectVertices(W_Z, W_A30);
        connectVertices(W_Z, W_AL_A10);
        connectVertices(W_AE, W_A48);
        connectVertices(W_AE, W_A28);
        connectVertices(W_AE, W_A30);
        connectVertices(W_AE, W_AI);
        connectVertices(W_AE, W_AL_A10);
        connectVertices(W_AE, W_A43);
        connectVertices(W_AE, W_AH);
        connectVertices(W_AE, Y_AE_AF);
        connectVertices(W_AE, W_AAT);
        connectVertices(W_AH, W_AAZ_A52);
        connectVertices(W_AH, AG_AH);
        connectVertices(W_AH, W_AI);
        connectVertices(W_AH, W_A30);
        connectVertices(W_AH, W_AL_A10);
        connectVertices(W_AH, W_A28);
        connectVertices(W_AH, W_A43);
        connectVertices(W_AH, W_A48);
        connectVertices(W_AH, W_AAT);
        connectVertices(W_AI, W_AAZ_A52);
        connectVertices(W_AI, W_A30);
        connectVertices(W_AI, W_AL_A10);
        connectVertices(W_AI, W_AAT);
        connectVertices(W_AI, W_A48);
        connectVertices(W_AI, W_A28);
        connectVertices(W_AI, W_A43);
        connectVertices(W_AL_A10, AJ_AL);
        connectVertices(W_AL_A10, AL_A59);
        connectVertices(W_AL_A10, AL_A60);
        connectVertices(W_AL_A10, AK_AL);
        connectVertices(W_AL_A10, AL_A12);
        connectVertices(W_AL_A10, AL_A13);
        connectVertices(W_AL_A10, AL_AV);
        connectVertices(W_AL_A10, AL_AW);
        connectVertices(W_AL_A10, AL_AAN);
        connectVertices(W_AL_A10, AL_A27);
        connectVertices(W_AL_A10, W_AAT);
        connectVertices(W_AL_A10, W_A48);
        connectVertices(W_AL_A10, W_A28);
        connectVertices(W_AL_A10, W_A43);
        connectVertices(W_AL_A10, W_A30);
        connectVertices(W_AL_A10, W_AAZ_A52);
        connectVertices(W_AL_A10, AJ_AL);
        connectVertices(W_AL_A10, AL_A11);
        connectVertices(W_AL_A10, A3_A4_A10);
        connectVertices(W_AAT, AAT_AAW);
        connectVertices(W_AAT, AAT_AAU);
        connectVertices(W_AAT, W_AAZ_A52);
        connectVertices(W_AAT, W_A30);
        connectVertices(W_AAT, W_A43);
        connectVertices(W_AAT, W_A28);
        connectVertices(W_AAZ_A52, AAV_A52);
        connectVertices(W_AAZ_A52, AAZ_A1_A2);
        connectVertices(W_AAZ_A52, W_A28);
        connectVertices(W_AAZ_A52, W_A43);
        connectVertices(W_AAZ_A52, W_A30);
        connectVertices(W_AAZ_A52, W_A48);
        connectVertices(W_A28, W_A43);
        connectVertices(W_A28, W_A48);
        connectVertices(W_A28, W_A30);
        connectVertices(W_A28, A28_A42);
        connectVertices(W_A30, A2_A3_A30);
        connectVertices(W_A30, W_A43);
        connectVertices(W_A30, W_A48);
        connectVertices(W_A43, W_A48);
        connectVertices(Y_AE_AF, AF_A55);
        connectVertices(Z_AA, Z_AC);
        connectVertices(Z_AA, AA_A44);
        connectVertices(Z_AC, Z_AD);
        connectVertices(Z_AD, Z_AA);
        connectVertices(Z_AA, AA_A49);

        //connections start with initial double letter vertex
        connectVertices(AA_A44, AA_A49);
        connectVertices(AF_A55, A54_A55);
        connectVertices(AG_A54, A54_A55);
        connectVertices(AG_AH, AG_A54);
        connectVertices(AJ_AL, AL_A59);
        connectVertices(AJ_AL, AL2_A39);
        connectVertices(AJ_AL, AL_A60);
        connectVertices(AJ_AL, AK_AL);
        connectVertices(AJ_AL, AL_A11);
        connectVertices(AJ_AL, AL_A12);
        connectVertices(AJ_AL, AL_A13);
        connectVertices(AJ_AL, AL_AV);
        connectVertices(AJ_AL, AL_AW);
        connectVertices(AJ_AL, AL_AAN);
        connectVertices(AJ_AL, AL_A27);
        connectVertices(AK_AL, AL_A60);
        connectVertices(AK_AL, AL_A59);
        connectVertices(AK_AL, AL_A11);
        connectVertices(AK_AL, AL_A12);
        connectVertices(AK_AL, AL_A13);
        connectVertices(AK_AL, AL_AV);
        connectVertices(AK_AL, AL_AW);
        connectVertices(AK_AL, AL_AAN);
        connectVertices(AK_AL, AL_A27);
        connectVertices(AL_AV, AL_AW);
        connectVertices(AL_AV, AL_A13);
        connectVertices(AL_AV, AL_A12);
        connectVertices(AL_AV, AL_A11);
        connectVertices(AL_AV, AL_A59);
        connectVertices(AL_AV, AL_A60);
        connectVertices(AL_AV, AL_AW);
        connectVertices(AL_AV, AL_AAN);
        connectVertices(AL_AV, AL_A27);
        connectVertices(AL_AW, AW_AL2);
        connectVertices(AL_AW, AL_AAN);
        connectVertices(AL_AW, AL_A27);
        connectVertices(AL_AW, AL_A13);
        connectVertices(AL_AW, AL_A12);
        connectVertices(AL_AW, AL_A11);
        connectVertices(AL_AW, AL_A60);
        connectVertices(AL_AW, AL_A59);
        connectVertices(AL_AAN, AL_A27);
        connectVertices(AL_AAN, AL_A13);
        connectVertices(AL_AAN, AL_A12);
        connectVertices(AL_AAN, AL_A11);
        connectVertices(AL_AAN, AL_A59);
        connectVertices(AL_AAN, AL_A60);
        connectVertices(AL_A11, AL_A12);
        connectVertices(AL_A11, A4_A5_A11);
        connectVertices(AL_A11, AL_A13);
        connectVertices(AL_A11, AL_A59);
        connectVertices(AL_A11, AL_A60);
        connectVertices(AL_A11, AL_A27);
        connectVertices(AL_A12, AL_A13);
        connectVertices(AL_A12, A5_A12);
        connectVertices(AL_A12, AL_A27);
        connectVertices(AL_A12, AL_A59);
        connectVertices(AL_A12, AL_A60);
        connectVertices(AL_A13, A5_A6_A13);
        connectVertices(AL_A13, AL_A27);
        connectVertices(AL_A13, AL_A59);
        connectVertices(AL_A13, AL_A60);
        connectVertices(AL_A27, AL2_A27);
        connectVertices(AL_A27, A26_A27);
        connectVertices(AL_A27, AAF_A27);
        connectVertices(AL_A27, A26_A27);
        connectVertices(AL_A27, AL_A59);
        connectVertices(AL_A27, AL_A60);
        connectVertices(AL_A59, AL_A60);
        connectVertices(AN_A30, AN_A51);
        connectVertices(AN_A30, A29_A30);
        connectVertices(AN_A30, AR_A30);
        connectVertices(AO_AAM, AAJ_AAM);
        connectVertices(AO_AAM, AAK_AAM);
        connectVertices(AO_AAM, AT_AAA_AAM_A41);
        connectVertices(AO_AAM, AAV_A9);
        connectVertices(AO_AAM, AAV_A52);
        connectVertices(AO_AS, AO_AAM);
        connectVertices(AO_AS, AO_AU);
        connectVertices(AO_AS, AR_AS);
        connectVertices(AO_AS, AS_AT);
        connectVertices(AO_AS, AAV_A52);
        connectVertices(AO_AS, AAV_A9);
        connectVertices(AO_AU, AO_A31);
        connectVertices(AO_AU, AO_AAM);
        connectVertices(AO_AU, AAV_A52);
        connectVertices(AO_AU, AAV_A9);
        connectVertices(AR_AS, AR_A30);
        connectVertices(AR_AS, AS_AT);
        connectVertices(AR_A30, A29_A30);
        connectVertices(AS_AT, AT_AAA_AAM_A41);
        connectVertices(AT_AAA_AAM_A41, AAK_AAM);
        connectVertices(AT_AAA_AAM_A41, AAJ_AAM);
        connectVertices(AW_AL2, AL2_AAP);
        connectVertices(AW_AL2, AL2_A36);
        connectVertices(AW_AL2, AL2_A27);
        connectVertices(AW_AL2, AL2_A25);
        connectVertices(AW_AL2, AL2_AAR);
        connectVertices(AW_AL2, AL2_AAQ);
        connectVertices(AW_AL2, AL2_AAP);
        connectVertices(AW_AL2, AL2_A36);
        connectVertices(AW_AL2, AL2_A37);
        connectVertices(AW_AL2, AL2_A38);
        connectVertices(AW_AL2, AL2_A39);
        connectVertices(AY_AAJ, AAH_AAJ);
        connectVertices(AY_AAJ, AAJ_AAM);
        connectVertices(AL2_A39, A39_A40);
        connectVertices(A1_A8_A9, AAZ_A1_A2);
        connectVertices(A1_A8_A9, AAV_A9);
        connectVertices(A1_A8_A9, AAY_A7_A8);
        connectVertices(A2_A3_A30, A3_A4_A10);
        connectVertices(A2_A3_A30, AAZ_A1_A2);
        connectVertices(A3_A4_A10, A4_A5_A11);
        connectVertices(A5_A12, A5_A6_A13);
        connectVertices(A5_A12, A4_A5_A11);
        connectVertices(A6_A7, AAY_A7_A8);
        connectVertices(A6_A7, A5_A6_A13);

        //connections start with initial triple letter vertex
        connectVertices(AAD_AAE, AAE_AAF);
        connectVertices(AAD_AAE, AAD_A33);
        connectVertices(AAE_AAF, AAF_AAG);
        connectVertices(AAE_AAF, AAF_LP2_LP3);
        connectVertices(AAE_AAF, AAF_A27);
        connectVertices(AAF_AAG, AAF_LP2_LP3);
        connectVertices(AAF_AAG, AAG_AAH);
        connectVertices(AAF_AAG, AAF_A27);
        connectVertices(AAF_A27, AL2_A27);
        connectVertices(AAF_A27, A26_A27);
        connectVertices(AAF_A27, AAF_LP2_LP3);
        connectVertices(AAF_LP2_LP3, LP1_LP2);
        connectVertices(AAF_LP2_LP3, AAK_LP3_LP4);
        connectVertices(AAG_AAH, AAH_AAI);
        connectVertices(AAG_AAH, AAH_AAJ);
        connectVertices(AAG_AAH, AAK_LP3_LP4);
        connectVertices(AAH_AAJ, AAK_LP3_LP4);
        connectVertices(AAH_AAJ, AAH_AAI);
        connectVertices(AAH_AAJ, AAJ_AAM);
        connectVertices(AAH_AAI, AAK_LP3_LP4);
        connectVertices(AAJ_AAM, AAK_AAM);
        connectVertices(AAK_AAM, AAK_LP3_LP4);
        connectVertices(AAK_LP3_LP4, LP4_LP5);
        connectVertices(AAT_AAU, AAT_AAW);
        connectVertices(AAV_A9, AAV_A52);
        connectVertices(AAY_A7_A8, AAY_A53);
        connectVertices(AL2_AAP, AL2_AAQ);
        connectVertices(AL2_AAP, AL2_A25);
        connectVertices(AL2_AAP, AL2_A27);
        connectVertices(AL2_AAP, AL2_AAR);
        connectVertices(AL2_AAP, AL2_A36);
        connectVertices(AL2_AAP, AL2_A37);
        connectVertices(AL2_AAP, AL2_A38);
        connectVertices(AL2_AAP, AL2_A39);
        connectVertices(AL2_AAQ, AL2_AAR);
        connectVertices(AL2_AAQ, AL2_A25);
        connectVertices(AL2_AAQ, AL2_A27);
        connectVertices(AL2_AAQ, AL2_A36);
        connectVertices(AL2_AAQ, AL2_A37);
        connectVertices(AL2_AAQ, AL2_A38);
        connectVertices(AL2_AAQ, AL2_A39);
        connectVertices(AL2_AAR, AL2_A25);
        connectVertices(AL2_AAR, AL2_A27);
        connectVertices(AL2_AAR, AL2_A36);
        connectVertices(AL2_AAR, AL2_A37);
        connectVertices(AL2_AAR, AL2_A38);
        connectVertices(AL2_AAR, AL2_A39);
        connectVertices(AL2_A25, AL2_A27);
        connectVertices(AL2_A25, AL2_A36);
        connectVertices(AL2_A25, AL2_A37);
        connectVertices(AL2_A25, AL2_A38);
        connectVertices(AL2_A25, AL2_A39);
        connectVertices(AL2_A27, A26_A27);
        connectVertices(AL2_A27, AL2_A36);
        connectVertices(AL2_A27, AL2_A37);
        connectVertices(AL2_A27, AL2_A38);
        connectVertices(AL2_A27, AL2_A39);
        connectVertices(AL2_A36, AL2_A38);
        connectVertices(AL2_A36, AL2_A39);
        connectVertices(AL2_A36, AL2_A37);
        connectVertices(AL2_A37, AL2_A38);
        connectVertices(AL2_A37, AL2_A39);
        connectVertices(AL2_A38, AL2_A39);
        connectVertices(A15_A16_A18, A17_A18_A19_A20);
        connectVertices(A17_A18_A19_A20, A20_A21);
        connectVertices(A17_A18_A19_A20, A19_A22);
        connectVertices(A19_A22, A22_A23_A24);
        connectVertices(A20_A21, A21_A23);
        connectVertices(A21_A23, A21_A24);
        connectVertices(A21_A23, A22_A23_A24);
        connectVertices(A21_A24, A22_A23_A24);
        connectVertices(LP1_LP5_LP6, LP4_LP5);
        connectVertices(LP1_LP2, LP1_LP5_LP6);

        //Agriculture
        connectVertices(agriculture_ent_AAQ, AL2_AAQ);
        connectVertices(agriculture_ent_AAP, AL2_AAP);

        addEntrance(agriculture, agriculture_ent_AAQ);
        addEntrance(agriculture, agriculture_ent_AAP);

        //Agriculture Engineer
        connectVertices(agri_engineer_ent_A36, AL2_A36);
        connectVertices(agri_engineer_ent_A37, AL2_A37);

        addEntrance(agri_engineer, agri_engineer_ent_A36);
        addEntrance(agri_engineer, agri_engineer_ent_A37);

        //Animal Sci
        connectVertices(animal_sci_ent_A25, AL2_A25);
        connectVertices(animal_sci_ent_AAR, AL2_AAR);

        addEntrance(animal_sci, animal_sci_ent_A25);
        addEntrance(animal_sci, animal_sci_ent_AAR);

        //Architecture 2
        connectVertices(archi2_ent_AAI, AAH_AAI);
        connectVertices(archi2_ent_A32, AAG_AAH);
        connectVertices(archi2_ent_AAI, fac_music_ent_AAI);

        addEntrance(architecture2, archi2_ent_AAI);
        addEntrance(architecture2, archi2_ent_A32);

        //Armes
        connectVertices(armes_ent_Q, Q_S);
        connectVertices(armes_parker_allen_ent_K, J_K);
        connectVertices(machray_armes_ent_A28, A28_A42);

        addEntrance(armes, armes_ent_Q);
        addEntrance(armes, armes_parker_allen_ent_K);
        addEntrance(parker, armes_parker_allen_ent_K);
        addEntrance(allen, armes_parker_allen_ent_K);

        //Artlab
        connectVertices(artlab_ent_A40, A39_A40);

        addEntrance(artlab, artlab_ent_A40);

        //Bio Science
        connectVertices(bio_sci_ent_P, P_A47);
        connectVertices(bio_sci_ent_R, R_S);

        addEntrance(bio_sci, bio_sci_ent_P);
        addEntrance(bio_sci, bio_sci_ent_R);

        //Buller
        connectVertices(buller_ent_U1, U_U1);
        connectVertices(buller_ent_U2, U_U2);
        connectVertices(buller_ent_U1, U_AAU);
        connectVertices(buller_ent_U2, U_AAW);

        addEntrance(buller, buller_ent_U1);
        addEntrance(buller, buller_ent_U2);

        //Dairy Sci
        connectVertices(dairy_sci_ent_A38, AL2_A38);

        addEntrance(dairy_sci, dairy_sci_ent_A38);

        //Drake
        //connectVertices(drake_ent_A39, AL2_A39);
        connectVertices(drake_ent_A39, A39_A40);
        connectVertices(drake_ent_A39, AL2_A39);

        addEntrance(drake, drake_ent_A39);

        //Duff Roblin
        connectVertices(duff_roblin_ent_A42, A28_A42);
        connectVertices(duff_roblin_ent_A43, W_A43);
        connectVertices(duff_roblin_ent_AB, V_AB);

        addEntrance(duff_roblin, duff_roblin_ent_A42);
        addEntrance(duff_roblin, duff_roblin_ent_A43);
        addEntrance(duff_roblin, duff_roblin_ent_AB);

        //Education
        connectVertices(education_ent_AAB, O_AAB);
        connectVertices(education_ent_AAC, O_AAA_AAC_LP6);
        connectVertices(education_ent_A33, AAD_A33);
        connectVertices(education_ent_A57, O_A57);

        addEntrance(education, education_ent_AAB);
        addEntrance(education, education_ent_AAC);
        addEntrance(education, education_ent_A33);
        addEntrance(education, education_ent_A57);

        //Elizabeth Dafoe
        connectVertices(eli_dafoe_lib_ent_Y_AE, Y_AE_AF);

        addEntrance(elizabeth_dafoe, eli_dafoe_lib_ent_Y_AE);

        //EITC 1 / 2 / 3
        connectVertices(eitc1_eitc3_ent_AV, AL_AV);
        connectVertices(eitc1_ent_AQ, S_AQ);
        connectVertices(eitc2_ent_AP, S_AP);
        connectVertices(eitc2_ent_A31, AO_A31);
        connectVertices(eitc2_ent_A31, S_AO_AAV);
        connectVertices(eitc3_ent_AU, AO_AU);

        addEntrance(eitc1, eitc1_eitc3_ent_AV);
        addEntrance(eitc1, eitc1_ent_AQ);
        addEntrance(eitc2, eitc2_ent_AP);
        addEntrance(eitc2, eitc2_ent_A31);
        addEntrance(eitc3, eitc1_eitc3_ent_AV);
        addEntrance(eitc3, eitc3_ent_AU);

        //Extended Education
        connectVertices(ext_educ_ent_A58, N_A58);
        connectVertices(ext_educ_ent_A26, A26_A27);

        addEntrance(ext_educ, ext_educ_ent_A58);
        addEntrance(ext_educ, ext_educ_ent_A26);

        //Faculty of Music
        connectVertices(fac_music_ent_AAI, AAH_AAI);
        connectVertices(fac_music_ent_AAN, AL_AAN);

        addEntrance(faculty_music, fac_music_ent_AAI);
        addEntrance(faculty_music, fac_music_ent_AAN);

        //Fletcher
        connectVertices(fletcher_ent_AH, AG_AH);
        connectVertices(fletcher_ent_AH, W_AH);

        addEntrance(fletcher, fletcher_ent_AH);

        //Helen Glass
        connectVertices(helen_glass_ent_A41, AT_AAA_AAM_A41);
        connectVertices(helen_glass_ent_A41, AAK_AAM);
        connectVertices(helen_glass_ent_A41, AAJ_AAM);
        connectVertices(helen_glass_ent_A41, AO_AAM);

        addEntrance(helen_glass, helen_glass_ent_A41);

        //Human Ecology
        connectVertices(human_eco_ent_A44, AA_A44);

        addEntrance(human_eco, human_eco_ent_A44);

        //Istbister
        connectVertices(istbister_ent_AK_A60, AK_AL);
        connectVertices(istbister_ent_AK_A60, AL_A60);

        addEntrance(istbister, istbister_ent_AK_A60);

        //Machray
        connectVertices(machray_armes_ent_A28, W_A28);

        addEntrance(machray, machray_armes_ent_A28);
        addEntrance(armes, machray_armes_ent_A28);

        //Parker
        connectVertices(parker_ent_A35, I_A35);

        addEntrance(parker, parker_ent_A35);

        //Robert Schultz
        connectVertices(rob_schutlz_ent_A14, M_A14);

        addEntrance(robert_schultz, rob_schutlz_ent_A14);

        //Robson
        connectVertices(robson_ent_AD, Z_AD);

        addEntrance(robson, robson_ent_AD);

        //Russel
        connectVertices(russel_ent_AW, AL_AW);
        connectVertices(russel_ent_AY, AY_AAJ);
        connectVertices(russel_ent_AW, AW_AL2);

        addEntrance(russel, russel_ent_AW);
        addEntrance(russel, russel_ent_AY);

        //St Johns
        connectVertices(st_john_ent_C, C_D);
        connectVertices(st_john_ent_A46, A45_A46);
        connectVertices(st_john_ent_A24_A23, A22_A23_A24);
        connectVertices(st_john_ent_A24_A23, A21_A23);
        connectVertices(rob_schult_st_johns_ent_A22_A19, A19_A22);

        addEntrance(st_johns, st_john_ent_C);
        addEntrance(st_johns, st_john_ent_A46);
        addEntrance(st_johns, st_john_ent_A24_A23);
        addEntrance(st_johns, rob_schult_st_johns_ent_A22_A19);
        addEntrance(robert_schultz, rob_schult_st_johns_ent_A22_A19);

        //St Pauls
        connectVertices(st_pauls_ent_A, A_D);
        connectVertices(st_pauls_ent_A16, A15_A16_A18);

        addEntrance(st_pauls, st_pauls_ent_A);
        addEntrance(st_pauls, st_pauls_ent_A16);

        //Tier
        connectVertices(tier_ent_AJ, AJ_AL);
        connectVertices(tier_ent_A59, AL_A59);
        connectVertices(tier_ent_AI, W_AI);
        connectVertices(tier_ent_AJ, AL2_A39);

        addEntrance(tier, tier_ent_AJ);
        //addEntrance(tier, tier_ent_A59);
        addEntrance(tier, tier_ent_AI);

        //University College
        connectVertices(uni_college_ent_AC, Z_AC);

        addEntrance(uni_college, uni_college_ent_AC);

        //University Centre
        connectVertices(uni_centre_ent_T, T_A50_A51);
        connectVertices(uni_centre_ent_AM, S_AM);
        connectVertices(uni_centre_ent_A29, A29_A30);
        connectVertices(uni_centre_ent_AS, AS_AT);
        connectVertices(uni_centre_ent_AS, AR_AS);
        connectVertices(uni_centre_ent_AS, AO_AS);

        addEntrance(uni_centre, uni_centre_ent_T);
        addEntrance(uni_centre, uni_centre_ent_AM);
        addEntrance(uni_centre, uni_centre_ent_A29);
        addEntrance(uni_centre, uni_centre_ent_AS);

        //Wallace
        connectVertices(wallace_ent, E_F_G);

        addEntrance(wallace, wallace_ent);
    }
}
