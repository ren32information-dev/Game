package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class CharacterRendererManager {
    private HashMap<CharacterState, ArrayList<CharacterRenderer>> pCharacterRenderers = new HashMap<>();
    // キャラクターの状態ごとのレンダラーリスト

    // コンストラクタ
    public CharacterRendererManager(Camera pCamera) {
        Init(pCamera);
    }

    public void Init(Camera pCamera) {
        pCharacterRenderers.put(CharacterState.STAND, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.JUMP, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.ATTACK, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.DAMAGE, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.DOWN, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.FRONT, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.GUARD, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.CROUCH, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.DASH, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.HEAVYATTACK5, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.MEDIUMATTACK5, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.LIGHTATTACK5, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.LIGHTATTACK2, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.HEAVYATTACK2, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.MEDIUMATTACK6, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.LIGHTJUMPATTACK, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.MEDIUMJUMPATTACK, new ArrayList<>());
        pCharacterRenderers.put(CharacterState.HEAVYJUMPATTACK, new ArrayList<>());

        

        // 立ち状態のレンダラーを追加
        ArrayList<CharacterRenderer> standRenderers = pCharacterRenderers.get(CharacterState.STAND);
        standRenderers.add(new CharacterRenderer(pCamera, "Image/St001.png"));

        // ジャンプ状態のレンダラーを追加
        ArrayList<CharacterRenderer> jumpRenderers = pCharacterRenderers.get(CharacterState.JUMP);
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/JpStart002.png"));
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/JpStart001.png"));
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/Jp001.png"));
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/Jp002.png"));
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/Jp003.png"));
        jumpRenderers.add(new CharacterRenderer(pCamera, "Image/JpEnd001.png"));

        // 攻撃状態のレンダラーを追加
        ArrayList<CharacterRenderer> attackRenderers = pCharacterRenderers.get(CharacterState.ATTACK);

        // 被ダメージ状態のレンダラーを追加
        ArrayList<CharacterRenderer> damageRenderers = pCharacterRenderers.get(CharacterState.DAMAGE);
        damageRenderers.add(new CharacterRenderer(pCamera, "Image/Jp003.png"));

        // ダウン状態のレンダラーを追加
        ArrayList<CharacterRenderer> downRenderers = pCharacterRenderers.get(CharacterState.DOWN);

        // 前進状態のレンダラーを追加
        ArrayList<CharacterRenderer> frontRenderers = pCharacterRenderers.get(CharacterState.FRONT);
        frontRenderers.add(new CharacterRenderer(pCamera, "Image/Walk002.png"));
        frontRenderers.add(new CharacterRenderer(pCamera, "Image/Walk001.png"));

        // ガード状態のレンダラーを追加
        ArrayList<CharacterRenderer> guardRenderers = pCharacterRenderers.get(CharacterState.GUARD);

        // しゃがみ状態のレンダラーを追加
        ArrayList<CharacterRenderer> crouchRenderers = pCharacterRenderers.get(CharacterState.CROUCH);
        crouchRenderers.add(new CharacterRenderer(pCamera, "Image/CrStart001.png"));
        crouchRenderers.add(new CharacterRenderer(pCamera, "Image/Cr001.png"));

        // ダッシュ状態のレンダラーを追加
        ArrayList<CharacterRenderer> dashRenderers = pCharacterRenderers.get(CharacterState.DASH);
        dashRenderers.add(new CharacterRenderer(pCamera, "Image/DaStart001.png"));
        dashRenderers.add(new CharacterRenderer(pCamera, "Image/Da001.png"));
        dashRenderers.add(new CharacterRenderer(pCamera, "Image/Da003.png"));
        dashRenderers.add(new CharacterRenderer(pCamera, "Image/Da002.png"));

        // 強立攻撃
        ArrayList<CharacterRenderer> heavyAttack5Renderers = pCharacterRenderers.get(CharacterState.HEAVYATTACK5);
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHighStart001.png"));
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHighStart002.png"));
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHighStart003.png"));
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHigh002.png"));
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHigh001.png"));
        heavyAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoHighEnd001.png"));

        // 中立攻撃
        ArrayList<CharacterRenderer> mediumAttack5Renderers = pCharacterRenderers.get(CharacterState.MEDIUMATTACK5);
        mediumAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoMidStart001.png"));
        mediumAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoMid002.png"));
        mediumAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoMid001.png"));

        // 弱攻撃
        ArrayList<CharacterRenderer> lightAttack5Renderers = pCharacterRenderers.get(CharacterState.LIGHTATTACK5);
        lightAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoLowStart001.png"));
        lightAttack5Renderers.add(new CharacterRenderer(pCamera, "Image/AtkDefoLow001.png"));

        // しゃがみ弱
        ArrayList<CharacterRenderer> lightAttack2Renderers = pCharacterRenderers.get(CharacterState.LIGHTATTACK2);
        lightAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchLowStart001.png"));
        lightAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchLow001.png"));

        // しゃがみ強
        ArrayList<CharacterRenderer> heavyAttack2Renderers = pCharacterRenderers.get(CharacterState.HEAVYATTACK2);
        heavyAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchHighStart002.png"));
        heavyAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchHighStart001.png"));
        heavyAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchHigh001.png"));
        heavyAttack2Renderers.add(new CharacterRenderer(pCamera, "Image/AtkCrouchHighEnd001.png"));

        // 前中
        ArrayList<CharacterRenderer> mediumAttack6Renderers = pCharacterRenderers.get(CharacterState.MEDIUMATTACK6);
        mediumAttack6Renderers.add(new CharacterRenderer(pCamera, "Image/AtkFrontMidStart003.png"));
        mediumAttack6Renderers.add(new CharacterRenderer(pCamera, "Image/AtkFrontMidStart002.png"));
        mediumAttack6Renderers.add(new CharacterRenderer(pCamera, "Image/AtkFrontMidStart001.png"));
        mediumAttack6Renderers.add(new CharacterRenderer(pCamera, "Image/AtkFrontMid001.png"));
        mediumAttack6Renderers.add(new CharacterRenderer(pCamera, "Image/AtkFrontMidEnd001.png"));

        // ジャンプ弱
        ArrayList<CharacterRenderer> lightAttackRenderers = pCharacterRenderers.get(CharacterState.LIGHTJUMPATTACK);
        lightAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkLowStart001.png"));
        lightAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkLow001.png"));

        // ジャンプ中
        ArrayList<CharacterRenderer> mediumAttackRenderers = pCharacterRenderers.get(CharacterState.MEDIUMJUMPATTACK);
        mediumAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkMidStart001.png"));
        mediumAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkMid001.png"));
        mediumAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkMidEnd001.png"));

        // ジャンプ強
        ArrayList<CharacterRenderer> heavyAttackRenderers = pCharacterRenderers.get(CharacterState.HEAVYJUMPATTACK);
        heavyAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkHighStart002.png"));
        heavyAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkHighStart001.png"));
        heavyAttackRenderers.add(new CharacterRenderer(pCamera, "Image/JpAtkHigh001.png"));
    }

    public void Uninit() {
        for (ArrayList<CharacterRenderer> renderers : pCharacterRenderers.values()) {
            for (CharacterRenderer renderer : renderers) {
                renderer.Release();
            }
            renderers.clear();
        }
        pCharacterRenderers.clear();
    }

    public CharacterRenderer GetRenderer(CharacterState state, int index) {
        ArrayList<CharacterRenderer> renderers = pCharacterRenderers.get(state);
        if (renderers != null && index >= 0 && index < renderers.size()) {
            return renderers.get(index);
        }
        return null;
    }

    public void DrawCharacter(Character pCharacter, int frame) {
        CharacterState state = pCharacter.GetCurrentState();
        ArrayList<CharacterRenderer> renderers = pCharacterRenderers.get(state);
        if (renderers != null && !renderers.isEmpty()) {
            int index = frame % renderers.size();
            CharacterRenderer renderer = renderers.get(index);
            renderer.DrawCharacter(pCharacter);
        }
    }
}
