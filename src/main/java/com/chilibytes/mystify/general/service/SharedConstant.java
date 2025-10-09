package com.chilibytes.mystify.general.service;

import lombok.Getter;
import lombok.Setter;

public class SharedConstant {

    private SharedConstant() {

    }

    @Getter
    @Setter
    private static boolean multiLayerImage = false;
}
