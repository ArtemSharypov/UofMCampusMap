package com.artem.uofmcampusmap;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Artem on 2017-04-28.
 */

public class MapGraph {
    private HashMap<String, ArrayList<Vertex>> startEndLocations;
    private Context context;

    public MapGraph(Context context)
    {
        startEndLocations = new HashMap<>();
        this.context = context;
        populateGraph();
    }

    private void addVertex(String keyName, ArrayList<Vertex> entranceVertices)
    {
        if(startEndLocations.containsKey(keyName))
        {
            entranceVertices.addAll(startEndLocations.get(keyName));
        }

        startEndLocations.put(keyName, entranceVertices);
    }

    private void addVertex(String keyName, Vertex entranceVertice)
    {
        ArrayList<Vertex> locationsList = new ArrayList<>();

        if(startEndLocations.containsKey(keyName))
        {
            locationsList = startEndLocations.get(keyName);
        }

        locationsList.add(entranceVertice);
        startEndLocations.put(keyName, locationsList);
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
        Vertex agri_engineer_ent_A37 = new Vertex(agri_engineer, new LatLng(49.807491, -97.133790));

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
        Vertex artlab_ent_A40 = new Vertex(artlab, new LatLng(49.808648, -97.130393));

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
        Vertex ext_educ_ent_A25 = new Vertex(ext_educ, new LatLng(49.807550, -97.137832));
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
        Vertex robson_ent_AD = new Vertex(robson, new LatLng(49.811751, -97.131005));

        String russel = context.getResources().getResourceName(R.string.russel);
        Vertex russel_ent_AW = new Vertex(russel, new LatLng(49.807901, -97.135211));
        Vertex russel_ent_AY = new Vertex(russel, new LatLng(49.808198, -97.135456));

        String st_johns = context.getResources().getResourceName(R.string.st_johns);
        Vertex st_john_ent_C  = new Vertex(st_johns, new LatLng(49.810899, -97.136611));
        Vertex st_john_ent_A46 = new Vertex(st_johns, new LatLng(49.810748, -97.137068));
        Vertex st_john_ent_A24_A23 = new Vertex(st_johns, new LatLng(49.810549, -97.137036));
        Vertex rob_schult_st_johns_ent_A22_A19 = new Vertex(st_johns, new LatLng(49.810200, -97.136742));

        String st_pauls = context.getResources().getResourceName(R.string.st_pauls);
        Vertex st_pauls_ent_A = new Vertex(st_pauls, new LatLng(49.810458, -97.138114));
        Vertex st_pauls_ent_A16 = new Vertex(st_pauls, new LatLng(49.809881, -97.137395));

        String tier = context.getResources().getResourceName(R.string.tier);
        Vertex tier_ent_AJ = new Vertex(tier, new LatLng(49.808939, -97.130748));
        Vertex tier_ent_A29 = new Vertex(tier, new LatLng(49.808866, -97.130578)); //mainly for tunnels / lower floor so not important ish

        String uni_college = context.getResources().getResourceName(R.string.uni_college);
        Vertex uni_college_ent_AC = new Vertex(uni_college, new LatLng(49.811055, -97.131105));

        String uni_centre = context.getResources().getResourceName(R.string.uni_centre);
        Vertex uni_centre_ent_T = new Vertex(uni_centre, new LatLng(49.809453, -97.133924));
        Vertex uni_centre_ent_AM = new Vertex(uni_centre, new LatLng(49.809290, -97.133710));
        Vertex uni_centre_ent_A29 = new Vertex(uni_centre, new LatLng(49.809058, -97.135026));
        Vertex uni_centre_ent_AS = new Vertex(uni_centre, new LatLng(49.808777, -97.135054));

        String wallace = context.getResources().getResourceName(R.string.wallace);
        Vertex wallace_ent = new Vertex(wallace, new LatLng(49.811639, -97.135697));

        //Other points
        Vertex E_F = new Vertex(new LatLng(49.811589, -97.135828));
        Vertex E_F_G = new Vertex(new LatLng(49.811588, -97.135712));
        Vertex E_D_H = new Vertex(new LatLng(49.811423, -97.136240));
        Vertex D_C = new Vertex(new LatLng(49.811250, -97.136879));
        Vertex D_B = new Vertex(new LatLng(49.810992, -97.137633));
        Vertex A45_A46 = new Vertex(new LatLng(49.810794, -97.137122));
        Vertex D_A = new Vertex(new LatLng(49.810733, -97.138385));
        Vertex B_A21_A45 = new Vertex(new LatLng(49.810656, -97.137366));
        Vertex A24_A23_A22 = new Vertex(new LatLng(49.810511, -97.137170));
        Vertex A24_A21 = new Vertex(new LatLng(49.810568, -97.137337));
        Vertex A23_A21 = new Vertex(new LatLng(49.810435, -97.137310));
        Vertex A22_A19 = new Vertex(new LatLng(49.810204, -97.136786));
        Vertex A21_A20 = new Vertex(new LatLng(49.810132, -97.137196));
        Vertex A20_A18_A19_A17 = new Vertex(new LatLng(49.810037, -97.137051));
        Vertex A18_A16_A15 = new Vertex(new LatLng(49.809929, -97.137234));
        Vertex A17_M = new Vertex(new LatLng(49.809745, -97.136707));
        Vertex A15_M = new Vertex(new LatLng(49.809707, -97.136842));
        Vertex A14_M = new Vertex(new LatLng(49.809884, -97.136281));
        Vertex F_I = new Vertex(new LatLng(49.811333, -97.135826));
        Vertex G_I = new Vertex(new LatLng(49.811510, -97.135103));
        Vertex I_H = new Vertex(new LatLng(49.811285, -97.136112));
        Vertex A35_I = new Vertex(new LatLng(49.811592, -97.134816));
        Vertex J_H = new Vertex(new LatLng(49.810654, -97.135593));
        Vertex J_K = new Vertex(new LatLng(49.810955, -97.134620));
        Vertex H_L = new Vertex(new LatLng(49.810354, -97.135364));
        Vertex L_M = new Vertex(new LatLng(49.810113, -97.135576));
        Vertex Q_P = new Vertex(new LatLng(49.810205, -97.135152));
        Vertex L_A47 = new Vertex(new LatLng(49.810160, -97.135529));
        Vertex Q_A47 = new Vertex(new LatLng(49.810134, -97.135267));
        Vertex Q_S = new Vertex(new LatLng(49.810502, -97.134322));
        Vertex R_S = new Vertex(new LatLng(49.810356, -97.134215));
        Vertex S_U = new Vertex(new LatLng(49.810097, -97.134001));
        Vertex U_AAU = new Vertex(new LatLng(49.810191, -97.133445));
        Vertex U_AAW = new Vertex(new LatLng(49.810324, -97.133047));
        Vertex U_U1 = new Vertex(new LatLng(49.810265, -97.133501));
        Vertex U_U2 = new Vertex(new LatLng(49.810395, -97.133117));
        Vertex U_W = new Vertex(new LatLng(49.810539, -97.132721));
        Vertex A28_W = new Vertex(new LatLng(49.811049, -97.133148));
        Vertex V_W = new Vertex(new LatLng(49.810752, -97.132914));
        Vertex V_A48 = new Vertex(new LatLng(49.810784, -97.132799));
        Vertex W_A48 = new Vertex(new LatLng(49.810688, -97.132843));
        Vertex V_A49 = new Vertex(new LatLng(49.810899, -97.132447));
        Vertex AA_A49 = new Vertex(new LatLng(49.810814, -97.132467));
        Vertex V_AB = new Vertex(new LatLng(49.810954, -97.132282));
        Vertex W_A43 = new Vertex(new LatLng(49.810870, -97.133014));
        Vertex A28_A42 = new Vertex(new LatLng(49.811276, -97.132537));
        Vertex A44_AA = new Vertex(new LatLng(49.810692, -97.132372));
        Vertex W_Z = new Vertex(new LatLng(49.810361, -97.132558));
        Vertex AA_Z = new Vertex(new LatLng(49.810406, -97.132402));
        Vertex Z_AC = new Vertex(new LatLng(49.810919, -97.130977));
        Vertex AD_Z = new Vertex(new LatLng(49.811158, -97.130407));
        Vertex AAT_AAW = new Vertex(new LatLng(49.810221, -97.132566));
        Vertex AAT_W = new Vertex(new LatLng(49.810253, -97.132450));
        Vertex AAT_AAU = new Vertex(new LatLng(49.809871, -97.133596));
        Vertex AAT_S = new Vertex(new LatLng(49.809821, -97.133766));
        Vertex A50_S = new Vertex(new LatLng(49.809618, -97.133597));
        Vertex T_A50_A51 = new Vertex(new LatLng(49.809489, -97.133801));
        Vertex AN_A51 = new Vertex(new LatLng(49.809261, -97.133832));
        Vertex AM_S = new Vertex(new LatLng(49.809366, -97.133421));
        Vertex Y_W = new Vertex(new LatLng(49.809994, -97.132167));
        Vertex AE_W = new Vertex(new LatLng(49.809734, -97.131940));
        Vertex Y_AE_AF = new Vertex(new LatLng(49.809901, -97.131709));
        Vertex AF_A55 = new Vertex(new LatLng(49.809799, -97.131503));
        Vertex A55_A54 = new Vertex(new LatLng(49.809686, -97.131429));
        Vertex A54_AG = new Vertex(new LatLng(49.809604, -97.131482));
        Vertex AG_AH = new Vertex(new LatLng(49.809546, -97.131433));
        Vertex AH_W = new Vertex(new LatLng(49.809470, -97.131720));
        Vertex AI_W = new Vertex(new LatLng(49.809117, -97.131423));
        Vertex AL_W_A10 = new Vertex(new LatLng(49.808644, -97.131080));
        Vertex AK_AL = new Vertex(new LatLng(49.808964, -97.130162));
        Vertex A28_AL = new Vertex(new LatLng(49.808844, -97.130504));
        Vertex AJ_AL = new Vertex(new LatLng(49.808791, -97.130653));
        Vertex A29_AL = new Vertex(new LatLng(49.808822, -97.130542));
        Vertex AAV_A52 = new Vertex(new LatLng(49.809436, -97.132023));
        Vertex A52_W_AAZ = new Vertex(new LatLng(49.809397, -97.131698));
        Vertex AAV_S = new Vertex(new LatLng(49.809091, -97.133163));
        Vertex AAV_A9 = new Vertex(new LatLng(49.809283, -97.132460));
        Vertex W_A30 = new Vertex(new LatLng(49.809030, -97.131369));
        Vertex AAZ_A1_A2 = new Vertex(new LatLng(49.809086, -97.131898));
        Vertex A30_A2_A3 = new Vertex(new LatLng(49.808936, -97.131622));
        Vertex A3_A4_A10 = new Vertex(new LatLng(49.808703, -97.131580));
        Vertex A4_A5_A11 = new Vertex(new LatLng(49.808567, -97.131712));
        Vertex A6_A5_A13 = new Vertex(new LatLng(49.808480, -97.132224));
        Vertex A5_A12 = new Vertex(new LatLng(49.808476, -97.132063));
        Vertex A6_A7 = new Vertex(new LatLng(49.808655, -97.132539));
        Vertex A7_A8_AAY = new Vertex(new LatLng(49.808895, -97.132538));
        Vertex A8_A1_A9 = new Vertex(new LatLng(49.809073, -97.132298));
        Vertex A11_AL = new Vertex(new LatLng(49.808419, -97.131737));
        Vertex A12_AL = new Vertex(new LatLng(49.808385, -97.131824));
        Vertex A13_AL = new Vertex(new LatLng(49.808248, -97.132312));
        Vertex AAY_A53 = new Vertex(new LatLng(49.808967, -97.132845));
        Vertex A53_S = new Vertex(new LatLng(49.808894, -97.133020));
        Vertex S_AL = new Vertex(new LatLng(49.808216, -97.132490));
        Vertex AQ_S = new Vertex(new LatLng(49.808559, -97.132751));
        Vertex AP_S = new Vertex(new LatLng(49.808764, -97.132921));
        Vertex AO_S = new Vertex(new LatLng(49.809083, -97.133179));
        Vertex AO_A31 = new Vertex(new LatLng(49.809024, -97.133488));
        Vertex AN_A30 = new Vertex(new LatLng(49.808888, -97.134933));
        Vertex A30_AR = new Vertex(new LatLng(49.808771, -97.134822));
        Vertex AR_AS = new Vertex(new LatLng(49.808707, -97.135006));
        Vertex AS_AO = new Vertex(new LatLng(49.808601, -97.134933));
        Vertex AS_AT = new Vertex(new LatLng(49.808753, -97.135040));
        Vertex A41_AAA_AT_AAM = new Vertex(new LatLng(49.808734, -97.135606));
        Vertex AAA_O = new Vertex(new LatLng(49.808800, -97.136022));
        Vertex AO_AU = new Vertex(new LatLng(49.808763, -97.134453));
        Vertex AO_AAM = new Vertex(new LatLng(49.808427, -97.135449));
        Vertex A30_A29 = new Vertex(new LatLng(49.809041, -97.135083));
        Vertex M_O = new Vertex(new LatLng(49.809709, -97.136797));
        Vertex O_A45 = new Vertex(new LatLng(49.809201, -97.136399));
        Vertex O_AAB = new Vertex(new LatLng(49.808819, -97.136071));
        Vertex O_AAA_AAC_LP6 = new Vertex(new LatLng(49.808720, -97.136015));
        Vertex LP6_LP5_LP1 = new Vertex(new LatLng(49.808573, -97.136018));
        Vertex LP5_LP4 = new Vertex(new LatLng(49.808503, -97.135880));
        Vertex LP4_AAK_LP3 = new Vertex(new LatLng(49.808386, -97.135833));
        Vertex LP3_LP2_AAF = new Vertex(new LatLng(49.808369, -97.136263));
        Vertex LP1_LP2 = new Vertex(new LatLng(49.808490, -97.136255));
        Vertex AAM_AAK = new Vertex(new LatLng(49.808485, -97.135491));
        Vertex AAM_AAJ = new Vertex(new LatLng(49.808351, -97.135394));
        Vertex AAJ_AY = new Vertex(new LatLng(49.808297, -97.135545));
        Vertex AAJ_AAH = new Vertex(new LatLng(49.808173, -97.135956));
        Vertex AAH_AAG = new Vertex(new LatLng(49.808004, -97.136044));
        Vertex AAH_AAI = new Vertex(new LatLng(49.807683, -97.136191));
        Vertex AAO_AAN = new Vertex(new LatLng(49.807207, -97.135645));
        Vertex AL_AAN = new Vertex(new LatLng(49.807194, -97.135541));
        Vertex AV_AL = new Vertex(new LatLng(49.807799, -97.133741));
        Vertex AW_AL = new Vertex(new LatLng(49.807411, -97.134860));
        Vertex AAE_AAF = new Vertex(new LatLng(49.808224, -97.136760));
        Vertex AAG_AAF = new Vertex(new LatLng(49.808238, -97.136637));
        Vertex A32_AAG = new Vertex(new LatLng(49.808053, -97.136251));
        Vertex AAE_AAD = new Vertex(new LatLng(49.808364, -97.137251));
        Vertex AAD_A33 = new Vertex(new LatLng(49.808431, -97.137319));
        Vertex N_AAF = new Vertex(new LatLng(49.807898, -97.137776));
        Vertex N_A25 = new Vertex(new LatLng(49.807579, -97.137750));
        Vertex A27_AAF = new Vertex(new LatLng(49.807250, -97.139614));
        Vertex A27_A26 = new Vertex(new LatLng(49.807031, -97.139409));
        Vertex AL_A27 = new Vertex(new LatLng(49.806183, -97.138606));
        Vertex A27_AL2 = new Vertex(new LatLng(49.805955, -97.138535));
        Vertex A25_AL2 = new Vertex(new LatLng(49.806084, -97.138131));
        Vertex AAR_AL2 = new Vertex(new LatLng(49.806269, -97.137642));
        Vertex AAQ_AL2 = new Vertex(new LatLng(49.807007, -97.135533));
        Vertex AAP_AL2 = new Vertex(new LatLng(49.807148, -97.135114));
        Vertex A36_AL2 = new Vertex(new LatLng(49.807462, -97.134166));
        Vertex A37_AL2 = new Vertex(new LatLng(49.807552, -97.133820));
        Vertex A38_AL2 = new Vertex(new LatLng(49.807714, -97.133368));
        Vertex A39_AL2 = new Vertex(new LatLng(49.808686, -97.130599));
        Vertex A40_A39 = new Vertex(new LatLng(49.808638, -97.130480));
        Vertex AW_AL2 = new Vertex(new LatLng(49.807432, -97.134804));
        Vertex S_AL2 = new Vertex(new LatLng(49.808085, -97.132369));
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
}
