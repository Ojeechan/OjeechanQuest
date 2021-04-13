package classes.constants;

/**
 * 画像のパスをstatic enumで提供する定数クラス
 *
 * @author  Naoki Yoshikawa
 */
public class ImageResource {

    // アニメーションの種類を表すラベル
    public static final String RUN_LEFT = "runLeft";
    public static final String RUN_RIGHT = "runRight";
    public static final String JUMP_LEFT = "jumpLeft";
    public static final String JUMP_RIGHT = "jumpRight";
    public static final String STAND_LEFT = "standLeft";
    public static final String STAND_RIGHT = "standRight";
    public static final String DUCK_LEFT = "duckLeft";
    public static final String DUCK_RIGHT = "duckRight";
    public static final String DUCK_STILL = "duckStill";
    public static final String GHOST_LEFT = "ghostLeft";
    public static final String GHOST_RIGHT = "ghostRight";
    public static final String OBJECT_GOAL = "goal";
    public static final String FRONT = "front";
    public static final String BACK = "back";
    public static final String HALFDOWN = "halfdown";
    public static final String HALFUP = "halfup";
    public static final String VINYL_RIGHT = "vinylRight";

    /**
     * アニメーションごとに使用する連続コマ画像を設定するインターフェース
     */
    public interface FrameBundle {
        /**
         * @return アニメーションラベル、または画像のファイルパス
         */
        String getValue();
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 現在、Player1RunRightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Player1RunLeft implements FrameBundle {
        LABEL(RUN_LEFT),
        PATH1("images/animations/character1_left_1.png"),
        PATH2("images/animations/character1_left_2.png"),
        PATH3("images/animations/character1_left_3.png"),
        PATH4("images/animations/character1_left_4.png"),
        PATH5("images/animations/character1_left_5.png"),
        PATH6("images/animations/character1_left_6.png"),
        PATH7("images/animations/character1_left_7.png"),
        PATH8("images/animations/character1_left_8.png");

        private final String path;

        private Player1RunLeft(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 左右の歩行時に使用する
     * </pre>
     */
    public static enum Player1RunRight implements FrameBundle {
        LABEL(RUN_RIGHT),
        PATH1("images/animations/character1_right_1.png"),
        PATH2("images/animations/character1_right_2.png"),
        PATH3("images/animations/character1_right_3.png"),
        PATH4("images/animations/character1_right_4.png"),
        PATH5("images/animations/character1_right_5.png"),
        PATH6("images/animations/character1_right_6.png"),
        PATH7("images/animations/character1_right_7.png"),
        PATH8("images/animations/character1_right_8.png");

        private final String value;

        Player1RunRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 現在、Player1StandRightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Player1StandLeft implements FrameBundle {
        LABEL(STAND_LEFT),
        PATH1("images/animations/character1_stand_left_1.png"),
        PATH2("images/animations/character1_stand_left_1.png"),
        PATH3("images/animations/character1_stand_left_1.png"),
        PATH4("images/animations/character1_stand_left_1.png"),
        PATH5("images/animations/character1_stand_left_1.png"),
        PATH6("images/animations/character1_stand_left_1.png"),
        PATH7("images/animations/character1_stand_left_2.png");

        private final String value;

        Player1StandLeft(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * プレイヤー停止時に使用する
     * </pre>
     */
    public static enum Player1StandRight implements FrameBundle {
        LABEL(STAND_RIGHT),
        PATH1("images/animations/character1_stand_right_2.png"),
        PATH2("images/animations/character1_stand_right_2.png"),
        PATH3("images/animations/character1_stand_right_2.png"),
        PATH4("images/animations/character1_stand_right_2.png"),
        PATH5("images/animations/character1_stand_right_2.png"),
        PATH6("images/animations/character1_stand_right_2.png"),
        PATH7("images/animations/character1_stand_right_1.png"),
        PATH8("images/animations/character1_stand_right_1.png"),
        PATH9("images/animations/character1_stand_right_1.png"),
        PATH10("images/animations/character1_stand_right_1.png"),
        PATH11("images/animations/character1_stand_right_1.png"),
        PATH12("images/animations/character1_stand_right_1.png");

        private final String value;

        Player1StandRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * しゃがみ移動時に使用する
     * </pre>
     */
    public static enum Player1DuckRight implements FrameBundle {
        LABEL(DUCK_RIGHT),
        PATH1("images/animations/character1_duck_right_1.png"),
        PATH2("images/animations/character1_duck_right_2.png");

        private final String value;

        Player1DuckRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * その場しゃがみ時に使用する
     * </pre>
     */
    public static enum Player1DuckStill implements FrameBundle {
        LABEL(DUCK_STILL),
        PATH1("images/animations/character1_duck_right_1.png");

        private final String value;

        Player1DuckStill(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 現在、Player1DuckRightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Player1DuckLeft implements FrameBundle {
        LABEL(DUCK_LEFT),
        PATH1("images/animations/character1_duck_left_1.png"),
        PATH2("images/animations/character1_duck_left_2.png");

        private final String value;

        Player1DuckLeft(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * ゲームオーバー時の幽霊スプライトで使用する
     * </pre>
     */
    public static enum Player1GhostRight implements FrameBundle {
        LABEL(GHOST_RIGHT),
        PATH1("images/animations/character1_ghost_right_1.png");

        private final String value;

        Player1GhostRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 現在、Player1GhostRightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Player1GhostLeft implements FrameBundle {
        LABEL(GHOST_LEFT),
        PATH1("images/animations/character1_ghost_left_1.png");

        private final String value;

        Player1GhostLeft(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * デバッグ用プレイヤー画像
     */
    public static enum DebuggerStandRight implements FrameBundle {
        LABEL(STAND_RIGHT),
        /*PATH1("images/animations/debugger2.png"),
        PATH2("images/animations/debugger2.png"),
        PATH3("images/animations/debugger2.png"),
        PATH4("images/animations/debugger2.png"),
        PATH5("images/animations/debugger2.png"),
        PATH6("images/animations/debugger2.png"),*/
        PATH7("images/animations/debugger.png"),
        PATH8("images/animations/debugger.png"),
        PATH9("images/animations/debugger.png"),
        PATH10("images/animations/debugger.png"),
        PATH11("images/animations/debugger.png"),
        PATH12("images/animations/debugger.png");

        private final String value;

        DebuggerStandRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static enum DebuggerRunRight implements FrameBundle {
        LABEL(RUN_RIGHT),
        /*PATH1("images/animations/debugger2.png"),
        PATH2("images/animations/debugger2.png"),
        PATH3("images/animations/debugger2.png"),
        PATH4("images/animations/debugger2.png"),
        PATH5("images/animations/debugger2.png"),
        PATH6("images/animations/debugger2.png"),*/
        PATH7("images/animations/debugger.png"),
        PATH8("images/animations/debugger.png"),
        PATH9("images/animations/debugger.png"),
        PATH10("images/animations/debugger.png"),
        PATH11("images/animations/debugger.png"),
        PATH12("images/animations/debugger.png");

        private final String value;

        DebuggerRunRight(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * アクションステージでの音声ブロック画像
     */
    public static enum VoiceIcon implements FrameBundle {
        LABEL("voiceIcon1"),
        PATH1("images/animations/a.png"),
        PATH2("images/animations/a_katakana.png");

        private final String value;

        VoiceIcon(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでの敵画像群
     * 現在、Enemy1Rightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Enemy1Left implements FrameBundle {
        LABEL(RUN_LEFT),
        PATH1("images/animations/enemy_left1.png"),
        PATH2("images/animations/enemy_left2.png");
        private final String value;

        Enemy1Left(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでの敵画像群
     * 左右の移動時に使用する
     * </pre>
     */
    public static enum Enemy1Right implements FrameBundle {
        LABEL(RUN_RIGHT),
        PATH1("images/animations/enemy_right1.png"),
        PATH2("images/animations/enemy_right2.png");
        private final String value;

        Enemy1Right(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * アクションステージでの背景画像群
     */
    public static enum LayeredBackground {
        SKY("images/backgrounds/background.png"),
        FOG("images/backgrounds/fog.png"),
        MOUNTAIN1("images/backgrounds/mountain1.png"),
        MOUNTAIN2("images/backgrounds/mountain2.png"),
        MOUNTAIN3("images/backgrounds/mountain3.png"),
        WINDOW("images/backgrounds/fukidashi.png"),
        BASE("images/backgrounds/background4_base.png"),
        LAYER1("images/backgrounds/background3_layer1.png"),
        LAYER2("images/backgrounds/background3_layer2.png"),
        LAYER3("images/backgrounds/background3_layer3.png"),
        WORLDMAP("images/backgrounds/worldmap.png"),
        GAMEOVER("images/backgrounds/gameover.png"),
        GOAL("images/backgrounds/goal.png"),
        GOAL2("images/backgrounds/goal2.png");

        private final String value;

        LayeredBackground(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * アクションステージでのステージ上に配置するオブジェクト群
     */
    public static enum StageObject {
        GOAL("images/objects/stageobject_goal.png"),
        START("images/objects/stageobject_start.png"),
        TREE("images/objects/stageobject_tree.png"),
        CAR1("images/objects/car.png"),
        CAR2("images/objects/car2.png"),
        CAR3("images/objects/car3.png"),
        MUSHROOM1("images/objects/mushroom1.png"),
        MUSHROOM2("images/objects/mushroom2.png"),
        MUSHROOM3("images/objects/mushroom3.png"),
        MUSHROOM4("images/objects/mushroom4.png"),
        ROADLIGHT("images/objects/roadlight.png"),
        WALL("images/objects/wall.png"),
        HOUSE("images/objects/house.png"),
        WORLDMAP_POINT("images/icons/worldmap_point.png"),
        BLOCK_C("images/blocks/block_c.png"),
        BLOCK_D("images/blocks/block_d.png"),
        BLOCK_E("images/blocks/block_e.png"),
        BLOCK_F("images/blocks/block_f.png"),
        BLOCK_G("images/blocks/block_g.png"),
        BLOCK_A("images/blocks/block_a.png"),
        BLOCK_B("images/blocks/block_b.png"),
        BLOCK_BEAT("images/blocks/block_beat.png"),
        BLOCK_STOP("images/blocks/block_stop.png");

        private final String value;

        StageObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * 各シーンで使用するロゴ画像
     * 起動画面、タイトル画面、ワールドマップで使用
     * </pre>
     */
    public static enum Logo {
        TITLE("images/logos/title_logo.png"),
        INTRODUCTION("images/logos/opening_logo.png"),
        INTRODUCTION2("images/logos/opening_logo2.png"),
        LOADING("images/logos/loading.png"),
        WORLDMAP("images/logos/worldmap_logo.png"),
        GO("images/logos/go.png");
        private final String value;

        Logo(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * フォント画像
     */
    public static enum Font {
        PA("images/fonts/font_pa.png"),
        GU("images/fonts/font_gu.png"),
        HA("images/fonts/font_ha.png"),
        JI("images/fonts/font_ji.png"),
        ME("images/fonts/font_me.png"),
        KA("images/fonts/font_ka.png"),
        RA("images/fonts/font_ra.png"),
        TSU("images/fonts/font_tsu.png"),
        DSU("images/fonts/font_dsu.png"),
        KI("images/fonts/font_ki.png"),
        KANA_SHI("images/fonts/font_kana_shi.png"),
        KANA_NO("images/fonts/font_kana_no.png"),
        DASH("images/fonts/font_dash.png"),
        KANA_KU("images/fonts/font_kana_ku.png"),
        KANA_BO("images/fonts/font_kana_bo.png"),
        KANA_TA("images/fonts/font_kana_ta.png"),
        KANA_I("images/fonts/font_kana_i.png"),
        KANA_TO("images/fonts/font_kana_to.png"),
        KANA_RU("images/fonts/font_kana_ru.png"),
        KANA_KI("images/fonts/font_kana_ki.png"),
        KANA_YA_SMALL("images/fonts/font_kana_ya_small.png"),
        KANA_SE("images/fonts/font_kana_se.png"),
        KANA_A("images/fonts/font_kana_a.png"),
        CHI("images/fonts/font_chi.png"),
        NI("images/fonts/font_ni.png"),
        E("images/fonts/font_e.png"),
        TO("images/fonts/font_to.png"),
        GO("images/fonts/font_go.png"),
        ZA("images/fonts/font_za.png"),
        SA("images/fonts/font_sa.png"),
        I("images/fonts/font_i.png"),
        KO("images/fonts/font_ko.png"),
        MO("images/fonts/font_mo.png"),
        U("images/fonts/font_u.png"),
        TSU_SMALL("images/fonts/font_tsu_small.png"),
        A("images/fonts/font_a.png"),
        RI("images/fonts/font_ri.png"),
        GA("images/fonts/font_ga.png"),
        NE("images/fonts/font_ne.png"),
        YA_SMALL("images/fonts/font_ya_small.png"),
        N("images/fonts/font_n.png"),
        SU("images/fonts/font_su.png"),
        RU("images/fonts/font_ru.png"),
        RE("images/fonts/font_re.png"),
        MA("images/fonts/font_ma.png"),
        NO("images/fonts/font_no.png"),
        MU("images/fonts/font_mu.png"),
        YO_SMALL("images/fonts/font_yo_small.png"),
        DO("images/fonts/font_do.png"),
        DE("images/fonts/font_de.png"),
        YA("images/fonts/font_ya.png"),
        KE("images/fonts/font_ke.png"),
        SHI("images/fonts/font_shi.png"),
        BE("images/fonts/font_be.png"),
        NA("images/fonts/font_na.png"),
        SE("images/fonts/font_se.png"),
        TA("images/fonts/font_ta.png"),
        QUESTION("images/fonts/font_question.png"),
        ZERO("images/fonts/font_zero.png"),
        ONE("images/fonts/font_one.png"),
        TWO("images/fonts/font_two.png"),
        THREE("images/fonts/font_three.png"),
        FOUR("images/fonts/font_four.png"),
        FIVE("images/fonts/font_five.png"),
        SIX("images/fonts/font_six.png"),
        SEVEN("images/fonts/font_seven.png"),
        EIGHT("images/fonts/font_eight.png"),
        NINE("images/fonts/font_nine.png"),
        ZERO_SEG("images/fonts/font_zero_seg.png"),
        ONE_SEG("images/fonts/font_one_seg.png"),
        TWO_SEG("images/fonts/font_two_seg.png"),
        THREE_SEG("images/fonts/font_three_seg.png"),
        FOUR_SEG("images/fonts/font_four_seg.png"),
        FIVE_SEG("images/fonts/font_five_seg.png"),
        SIX_SEG("images/fonts/font_six_seg.png"),
        SEVEN_SEG("images/fonts/font_seven_seg.png"),
        EIGHT_SEG("images/fonts/font_eight_seg.png"),
        NINE_SEG("images/fonts/font_nine_seg.png"),
        MINUS_SEG("images/fonts/font_minus.png"),
        KANA_WA("images/fonts/font_kana_wa.png"),
        KANA_DO("images/fonts/font_kana_do.png"),
        KANA_MA("images/fonts/font_kana_ma.png"),
        KANA_TSU_SMALL("images/fonts/font_kana_tsu_small.png"),
        KANA_PU("images/fonts/font_kana_pu.png"),
        KANA_SU("images/fonts/font_kana_su.png"),
        KANA_TE("images/fonts/font_kana_te.png"),
        KANA_MU("images/fonts/font_kana_mu.png"),
        KANA_E("images/fonts/font_kana_e.png"),
        KANA_N("images/fonts/font_kana_n.png"),
        KANA_DE("images/fonts/font_kana_de.png"),
        KANA_I_SMALL("images/fonts/font_kana_i_small.png"),
        KANA_YO_SMALL("images/fonts/font_kana_yo_small.png"),
        KANA_GU("images/fonts/font_kana_gu.png"),
        KANA_RO("images/fonts/font_kana_ro.png"),
        KANA_HO("images/fonts/font_kana_ho.png"),
        KANA_TSU("images/fonts/font_kana_tsu.png"),
        KANA_FU("images/fonts/font_kana_fu.png"),
        KANA_RI("images/fonts/font_kana_ri.png"),
        KANA_ZU("images/fonts/font_kana_zu.png"),
        KANA_RE("images/fonts/font_kana_re.png"),
        KANA_KO("images/fonts/font_kana_ko.png"),
        KANA_SA("images/fonts/font_kana_sa.png"),
        KANA_U("images/fonts/font_kana_u.png"),
        KU("images/fonts/font_ku.png"),
        O("images/fonts/font_o.png"),
        TE("images/fonts/font_te.png"),
        KANA_KA("images/fonts/font_kana_ka.png"),
        KANA_JI("images/fonts/font_kana_ji.png"),
        ALPH_A("images/fonts/font_alphabet_A.png"),
        ALPH_C("images/fonts/font_alphabet_C.png"),
        ALPH_D("images/fonts/font_alphabet_D.png"),
        ALPH_E("images/fonts/font_alphabet_E.png"),
        ALPH_F("images/fonts/font_alphabet_F.png"),
        ALPH_H("images/fonts/font_alphabet_H.png"),
        ALPH_K("images/fonts/font_alphabet_K.png"),
        ALPH_L("images/fonts/font_alphabet_L.png"),
        ALPH_N("images/fonts/font_alphabet_N.png"),
        ALPH_O("images/fonts/font_alphabet_O.png"),
        ALPH_P("images/fonts/font_alphabet_P.png"),
        ALPH_R("images/fonts/font_alphabet_R.png"),
        ALPH_S("images/fonts/font_alphabet_S.png"),
        ALPH_T("images/fonts/font_alphabet_T.png"),
        ALPH_U("images/fonts/font_alphabet_U.png"),
        ALPH_W("images/fonts/font_alphabet_W.png"),
        ALPH_B("images/fonts/font_alphabet_B.png"),
        ALPH_G("images/fonts/font_alphabet_G.png"),
        ALPH_M("images/fonts/font_alphabet_M.png"),
        ALPH_I("images/fonts/font_alphabet_I.png"),
        ALPH_J_SMALL("images/fonts/font_alphabet_j_small.png"),
        ALPH_E_SMALL("images/fonts/font_alphabet_e_small.png"),
        ALPH_C_SMALL("images/fonts/font_alphabet_c_small.png"),
        ALPH_H_SMALL("images/fonts/font_alphabet_h_small.png"),
        ALPH_A_SMALL("images/fonts/font_alphabet_a_small.png"),
        ALPH_N_SMALL("images/fonts/font_alphabet_n_small.png"),
        UP("images/fonts/font_arrow_up.png"),
        DOWN("images/fonts/font_arrow_down.png"),
        LEFT("images/fonts/font_arrow_left.png"),
        RIGHT("images/fonts/font_arrow_right.png"),
        COLON("images/fonts/font_colon.png"),
        DOT("images/fonts/font_dot.png"),
        SLASH("images/fonts/font_slash.png");

        private final String value;
        Font(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * スロットステージで使用する画像群
     */
    public static enum SlotIcon {
        REPLAY("images/icons/replay.png"),
        BELL("images/icons/bell.png"),
        CHERRY("images/icons/cherry.png"),
        SUIKA("images/icons/suika.png"),
        BAR("images/icons/bar.png"),
        SEVEN_RED("images/icons/seven_red.png"),
        SEVEN_BLUE("images/icons/seven_blue.png"),
        FRAME("images/icons/frame.png"),
        LAMP_TOP("images/icons/lamp_top.png"),
        LAMP_RIGHT("images/icons/lamp_right.png"),
        LAMP_LEFT("images/icons/lamp_left.png"),
        BUTTON("images/icons/button.png"),
        LEVER("images/icons/lever.png"),
        LEVER_DOWN("images/icons/lever_down.png"),
        SLOT("images/icons/slot.png"),
        CEIL_LIGHT("images/icons/ceil_light.png"),
        CHOP("images/icons/chop.png"),
        MOVIE1("images/animations/freeze_movie1.png"),
        MOVIE2("images/animations/freeze_movie2.png"),
        MOVIE3("images/animations/freeze_movie3.png"),
        MOVIE4("images/animations/freeze_movie4.png");

        private final String value;

        SlotIcon(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * レコードステージでのプレイヤー画像群
     * </pre>
     */
    public static enum PlayerFront implements FrameBundle {
        LABEL(FRONT),
        PATH1("images/animations/character1_front_1.png"),
        PATH2("images/animations/character1_front_2.png"),
        PATH3("images/animations/character1_front_1.png"),
        PATH4("images/animations/character1_front_3.png");

        private final String path;

        private PlayerFront(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }

    /**
     * <pre>
     * レコードステージでのプレイヤー画像群
     * </pre>
     */
    public static enum PlayerBack implements FrameBundle {
        LABEL(BACK),
        PATH1("images/animations/character1_back_1.png"),
        PATH2("images/animations/character1_back_2.png"),
        PATH3("images/animations/character1_back_1.png"),
        PATH4("images/animations/character1_back_3.png");

        private final String path;

        private PlayerBack(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }

    /**
     * <pre>
     * レコードステージでのプレイヤー画像群
     * </pre>
     */
    public static enum PlayerHalfdown implements FrameBundle {
        LABEL(HALFDOWN),
        PATH1("images/animations/character1_halfdown_1.png"),
        PATH2("images/animations/character1_halfdown_2.png"),
        PATH3("images/animations/character1_halfdown_1.png"),
        PATH4("images/animations/character1_halfdown_3.png");

        private final String path;

        private PlayerHalfdown(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }

    /**
     * <pre>
     * レコードステージでのプレイヤー画像群
     * </pre>
     */
    public static enum PlayerHalfup implements FrameBundle {
        LABEL(HALFUP),
        PATH1("images/animations/character1_halfup_1.png"),
        PATH2("images/animations/character1_halfup_2.png"),
        PATH3("images/animations/character1_halfup_1.png"),
        PATH4("images/animations/character1_halfup_3.png");

        private final String path;

        private PlayerHalfup(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }

    /**
     * レコードステージで使用する画像群
     */
    public static enum VinylIcon {
        VINYLBOX3_1("images/objects/vinylbox3_1.png"),
        VINYLBOX3_2("images/objects/vinylbox3_2.png"),
        VINYLBOX3_3("images/objects/vinylbox3_3.png"),
        VINYLBOX3_4("images/objects/vinylbox3_4.png"),
        VINYLBOX3_5("images/objects/vinylbox3_5.png"),
        VINYLBOX5_1("images/objects/vinylbox5_1.png"),
        VINYLBOX5_2("images/objects/vinylbox5_2.png"),
        VINYLBOX5_3("images/objects/vinylbox5_3.png"),
        VINYLBOX5_4("images/objects/vinylbox5_4.png"),
        VINYLBOX5_5("images/objects/vinylbox5_5.png"),
        TURNTABLE("images/objects/turntable.png"),
        OWNER("images/animations/owner.png"),
        CASHIER("images/objects/cashier.png"),
        TURNTABLE_CLOSE("images/objects/turntable_close.png"),
        VINYL1("images/objects/vinyl.png"),
        VINYL2("images/objects/vinyl2.png"),
        VINYL3("images/objects/vinyl3.png"),
        VINYL4("images/objects/vinyl4.png"),
        JACKET1("images/objects/jacket1.png"),
        JACKET2("images/objects/jacket2.png"),
        JACKET3("images/objects/jacket3.png"),
        JACKET4("images/objects/jacket4.png"),
        WINDOW("images/backgrounds/window.png"),
        ITEMWINDOW("images/backgrounds/itemwindow.png");

        private final String value;

        VinylIcon(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * <pre>
     * アクションステージでのプレイヤー画像群
     * 現在、Player1RunRightを反転させて使用しているためこちらは不使用
     * </pre>
     */
    public static enum Player1VinylRight implements FrameBundle {
        LABEL(VINYL_RIGHT),
        PATH1("images/animations/vinyl_right_1.png"),
        PATH2("images/animations/vinyl_right_2.png"),
        PATH3("images/animations/vinyl_right_3.png"),
        PATH4("images/animations/vinyl_right_4.png"),
        PATH5("images/animations/vinyl_right_5.png"),
        PATH6("images/animations/vinyl_right_6.png"),
        PATH7("images/animations/vinyl_right_7.png"),
        PATH8("images/animations/vinyl_right_8.png");

        private final String path;

        private Player1VinylRight(String path) {
            this.path = path;
        }

        public String getValue() {
            return path;
        }
    }
}
