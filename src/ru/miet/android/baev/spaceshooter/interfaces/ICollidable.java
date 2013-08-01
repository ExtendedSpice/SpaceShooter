package ru.miet.android.baev.spaceshooter.interfaces;

import ru.miet.android.baev.spaceshooter.ship.SpaceShip;

public interface ICollidable {
    boolean IsExplodable();
    SpaceShip GetOwner();
    void CollideWith(SpaceShip s, boolean vibrate);
}
