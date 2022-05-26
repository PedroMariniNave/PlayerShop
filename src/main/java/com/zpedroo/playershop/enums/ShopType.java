package com.zpedroo.playershop.enums;

import static com.zpedroo.playershop.utils.config.Settings.*;

public enum ShopType {
    BUY(BUY_TRANSLATION),
    SELL(SELL_TRANSLATION),
    BOTH(BOTH_TRANSLATION);

    private final String translation;

    ShopType(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}