package ru.miet.android.baev.spaceshooter.ship;

public class ControlUnit {
        private boolean switchState = false;

        public boolean GetState()
        {
            // ��� ���������
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
