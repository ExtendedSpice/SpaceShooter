package ru.miet.android.baev.spaceshooter.ship;

public class ControlUnit {
        private boolean switchState = false;

        public boolean GetState()
        {
            // для отрисовки
            return switchState;
        }
        public void SetState(boolean state)
        {
            switchState = state;
        }

        public void Switch()
        {
            switchState = !switchState;
        }
}
