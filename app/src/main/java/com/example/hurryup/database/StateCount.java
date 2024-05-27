package com.example.hurryup.database;

public class StateCount {
    public int state;
    public int count;

    @Override
    public boolean equals(Object object) {
        StateCount product = (StateCount) object;
        if (product.state == this.state) {
            return true;
        }
        return false;
    }
}
