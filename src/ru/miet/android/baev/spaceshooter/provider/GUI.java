package ru.miet.android.baev.spaceshooter.provider;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import ru.miet.android.baev.spaceshooter.ship.ControlUnit;

public class GUI {
	public enum ButtonType { LeftColumn, RightColumn, BothColumn, DefaultButton };

    static private float screenWidth;
    static private float screenHeight;
    static private float cellSize;

    static private ArrayList<GUIButtonUnit> leftColumn = new ArrayList<GUIButtonUnit>();
    static private ArrayList<GUIButtonUnit> rightColumn = new ArrayList<GUIButtonUnit>();
    static private ArrayList<GUIButtonUnit> defaultButton = new ArrayList<GUIButtonUnit>();
    static private ArrayList<GUIButtonUnit> allButtons = new ArrayList<GUIButtonUnit>();

    static private ArrayList<ControlUnit> allButtonControls = new ArrayList<ControlUnit>();
    static private ControlUnit fastCaptureMode;

    public static void SetScreen(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;

        // Размер ячейки формируется из эскиза - макс 4 квадрата слева и справа
        cellSize = height / 4f;

        GUIButtonUnit.SetPaintProps();
    }

    public static void Initialize()
    {
        leftColumn.clear();
        rightColumn.clear();
        defaultButton.clear();
        allButtons.clear();

        allButtonControls.clear();
        fastCaptureMode = new ControlUnit();
        AddButton(fastCaptureMode, ButtonType.DefaultButton);
    }

    public static void AddButton(ControlUnit control, ButtonType type)
    {
        // В этом методе добавляются кнопки в соответствующие списки
        allButtonControls.add(control);
        GUIButtonUnit button = new GUIButtonUnit(control);

        switch (type)
        {
            case LeftColumn:
                leftColumn.add(button);
                break;

            case RightColumn:
                rightColumn.add(button);
                break;

            case BothColumn:
                rightColumn.add(button);
                GUIButtonUnit button2 = new GUIButtonUnit(control);
                leftColumn.add(button2);
                allButtons.add(button2);
                break;

            case DefaultButton:
                defaultButton.add(button);
                break;
        }
        allButtons.add(button);
    }

    public static void FinalizeButtons()
    {
        // В зависимости от содержимого списков присваиваются границы кнопкам в этих списках
        CalculateButtonHeights(0, cellSize, leftColumn);
        CalculateButtonHeights(cellSize, screenWidth - cellSize, defaultButton);
        CalculateButtonHeights(screenWidth - cellSize, screenWidth, rightColumn);
    }

    private static void CalculateButtonHeights(float left, float right, ArrayList<GUIButtonUnit> buttons)
    {
        float buttonHeight = screenHeight / buttons.size();
        float top = 0;
        for(Iterator<GUIButtonUnit> i = buttons.iterator(); i.hasNext(); )
        {
            i.next().AssignRect(new RectF(left, top, right, top + buttonHeight));
            top += buttonHeight;
        }
    }

    public static void HandleTouchEvent(MotionEvent e)
    {

        if (allButtons.size() > 1)
        {
        	Iterator<GUIButtonUnit> i = allButtons.iterator();
        	
            if (e.getActionMasked() == MotionEvent.ACTION_UP)
            {
                i.next().Touch();
                if (fastCaptureMode.GetState())
                    for (; i.hasNext();)
                        i.next().FastTouch();
                else
                    for (; i.hasNext();)
                    	i.next().Touch();
            }

            else if (e.getActionMasked() == MotionEvent.ACTION_DOWN || e.getActionMasked() == MotionEvent.ACTION_MOVE)
            {
            	if(i.next().Touch(e))
                    Sounds.playClick();
            	
                if (fastCaptureMode.GetState())
                {
                    for (; i.hasNext();)
                    	i.next().FastTouch();

                    for (i = allButtons.iterator(); i.hasNext();)
                    	i.next().FastTouch(e);
                }
                else
                    for (; i.hasNext();)
                    	i.next().Touch(e);
            }
        }
    }

    public static void Draw(Canvas canvas)
    {
        for (Iterator<GUIButtonUnit> i = leftColumn.iterator(); i.hasNext();)
            i.next().Draw(canvas);
        for (Iterator<GUIButtonUnit> i = rightColumn.iterator(); i.hasNext();)
            i.next().Draw(canvas);
    }

    public static float GetCellSize()
    {
        return cellSize;
    }

    public static boolean GetControlMode()
    {
        return fastCaptureMode.GetState();
    }
    
    
    static class GUIButtonUnit
    {
        private static Paint paintPressed;
        private static Paint paintReleased;
        public static void SetPaintProps()
        {
            paintPressed = new Paint();
            paintPressed.setARGB(100, 200, 200, 200);
            paintPressed.setStyle(Paint.Style.FILL_AND_STROKE);
            paintPressed.setStrokeWidth( 1f);
            paintPressed.setStrokeCap(Paint.Cap.SQUARE);
            paintPressed.setStrokeJoin(Paint.Join.BEVEL);

            paintReleased = new Paint(paintPressed);
            paintReleased.setStyle(Paint.Style.STROKE);
        }

        private RectF activeRectangle;
        private Path drawContour;

        private ControlUnit controlUnit;
        private boolean pressed = false;
        private boolean pressedAlready = false;

        public GUIButtonUnit(ControlUnit source)
        {
            controlUnit = source;
        }
        public void AssignRect(RectF rect)
        {
            activeRectangle = rect;

            drawContour = new Path();
            drawContour.moveTo(rect.left + 1, rect.top + 1);
            drawContour.lineTo(rect.right - 1, rect.top + 1);
            drawContour.lineTo(rect.right - 1, rect.bottom - 1);
            drawContour.lineTo(rect.left + 1, rect.bottom - 1);
            drawContour.close();
        }

        public void Touch()
        {
            pressedAlready = false;
        }
        
        public boolean Touch(MotionEvent e)
        {
        	boolean result = false;
        	
            pressed = false;
            for (int i = 0; i < e.getPointerCount(); i++)
            {
                if (activeRectangle.contains(e.getX(i), e.getY(i)))
                    pressed = true;
            }
            if (pressed)
            {
                if (!pressedAlready)
                {
                    controlUnit.Switch();
                    result = true;
                    pressedAlready = true;
                }
            }
            else
                pressedAlready = false;
            
            return result;
        }

        public void FastTouch(MotionEvent e)
        {
            for (int i = 0; i < e.getPointerCount(); i++)
            {
                if (activeRectangle.contains(e.getX(i), e.getY(i)))
                {
                    controlUnit.SetState(true);
                }
            }
        }
        public void FastTouch()
        {
            controlUnit.SetState(false);
        }

        public void Draw(Canvas c)
        {
            c.drawPath(drawContour, controlUnit.GetState() ? paintPressed : paintReleased);
        }
    }
}


