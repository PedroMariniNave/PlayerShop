package com.zpedroo.playershop.utils.chat;

import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.objects.ShopCreator;

public class PlayerChat {

    private PlayerChatAction action;
    private PlayerChatAction next;
    private ShopAction shopAction;
    private ShopCreator creator;
    private Shop shop;

    public PlayerChat(PlayerChatAction action, PlayerChatAction next, ShopCreator creator) {
        this.action = action;
        this.next = next;
        this.creator = creator;
    }

    public PlayerChat(PlayerChatAction action, Shop shop) {
        this.action = action;
        this.shop = shop;
    }

    public PlayerChat(PlayerChatAction action, Shop shop, ShopAction shopAction) {
        this.action = action;
        this.shop = shop;
        this.shopAction = shopAction;
    }

    public PlayerChatAction getAction() {
        return action;
    }

    public PlayerChatAction getNext() {
        return next;
    }

    public ShopAction getShopAction() {
        return shopAction;
    }

    public ShopCreator getCreator() {
        return creator;
    }

    public Shop getShop() {
        return shop;
    }

    public enum PlayerChatAction {
        EDIT_BUY_PRICE,
        EDIT_SELL_PRICE,
        EDIT_AMOUNT,
        SELECT_AMOUNT
    }
}