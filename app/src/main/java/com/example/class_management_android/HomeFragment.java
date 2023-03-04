package com.example.class_management_android;

import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.plattysoft.leonids.ParticleSystem;

public class HomeFragment extends Fragment {

    private boolean setedBgrEffect = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        if (!setedBgrEffect)
        {
//            setupBgrEffect(v, container);
            setedBgrEffect = true;
        }

        return v;
    }


    private void setupBgrEffect(View v, ViewGroup container)
    {

        new ParticleSystem(getActivity(), 100, R.drawable.ic_aqua_confetti, 10000)
                .setParentViewGroup(container)
                .setFadeOut(2000)
                .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 90)
                .setRotationSpeed(200)
                .setAcceleration(0.000015f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 8);

        new ParticleSystem(getActivity(), 30, R.mipmap.ic_maple_foreground, 10000)
                .setParentViewGroup(container)
                .setFadeOut(3000)
                .setSpeedModuleAndAngleRange(0f, 0.3f, 0, 90)
                .setRotationSpeed(150)
                .setAcceleration(0.000003f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 10);

        new ParticleSystem(getActivity(), 10, R.mipmap.ic_sakura_foreground, 10000)
                .setParentViewGroup(container)
                .setFadeOut(2000)
                .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 60)
                .setRotationSpeed(800)
                .setAcceleration(0.000004f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 5);

        new ParticleSystem(getActivity(), 30, R.mipmap.ic_grape_foreground, 7500)
                .setParentViewGroup(container)
                .setFadeOut(3000)
                .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 60)
                .setRotationSpeed(200)
                .setAcceleration(0.000005f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 13);

        new ParticleSystem(getActivity(), 30, R.mipmap.ic_autumn_leaf_foreground, 8500)
                .setParentViewGroup(container)
                .setFadeOut(3000)
                .setSpeedModuleAndAngleRange(0f, 0.2f, 0, 90)
                .setRotationSpeed(200)
                .setAcceleration(0.00002f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 13);

        new ParticleSystem(getActivity(), 30, R.mipmap.ic_red_gold_leaf_foreground, 9000)
                .setParentViewGroup(container)
                .setFadeOut(3000)
                .setSpeedModuleAndAngleRange(0f, 0.1f, 0, 90)
                .setRotationSpeed(300)
                .setAcceleration(0.00002f, 90)
                .emit(v.findViewById(R.id.emiter_top_left), 13);
    }
}