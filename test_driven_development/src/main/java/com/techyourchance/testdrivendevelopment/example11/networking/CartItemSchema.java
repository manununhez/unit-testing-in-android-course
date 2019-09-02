package com.techyourchance.testdrivendevelopment.example11.networking;

import java.util.Objects;

public class CartItemSchema {
    private final String mId;
    private final String mTitle;
    private final String mDescription;
    private final int mPrice;

    public CartItemSchema(String id, String title, String description, int price) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mPrice = price;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getPrice() {
        return mPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItemSchema that = (CartItemSchema) o;
        return mPrice == that.mPrice &&
                mId.equals(that.mId) &&
                mTitle.equals(that.mTitle) &&
                mDescription.equals(that.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mDescription, mPrice);
    }
}
