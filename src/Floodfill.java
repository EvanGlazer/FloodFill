import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Floodfill extends Applet implements MouseListener
{
	Color m_objSelectedColor = Color.blue;
	int m_nSelectedColor = 0xff0000ff;
	BufferedImage m_objShape;
	MediaTracker tracker = new MediaTracker(this);
	int targetColor;
	int m_nTestShapeX = 100;
	int m_nTestShapeY = 100;
	
	static Color[] m_Colors =
	{
		Color.blue, Color.red, Color.green, Color.yellow,
		Color.gray, Color.magenta, Color.orange, Color.cyan
	};
	
	int m_nUpperLeftX = 10;
	int m_nUpperLeftY = 10;
	int m_nColorWidth = 50;
	int m_nColorHeight = 50;
	int m_nLowerRightX;
	int m_nLowerRightY;
	
    CheckboxGroup lngGrp = new CheckboxGroup();
    Checkbox full = new Checkbox("Full Recursion", lngGrp, true);
    Checkbox partial = new Checkbox("Partial Recursion", lngGrp, true);
 
	public void init()
	{
		addMouseListener(this);
        setSize(1020,700);

        add(partial);
        add(full);
        
        try 
        {
			m_objShape = ImageIO.read(Floodfill.class.getResourceAsStream("Untitled.png"));
			tracker.addImage(m_objShape, 100);
			tracker.waitForAll();
		} 
        catch (Exception e1) 
        {
		}
		
	}

	void DrawColors( Graphics canvas )
	{
		for( int i=0; i<m_Colors.length; i++ )
		{
			canvas.setColor( m_Colors[i] );
			canvas.fillRect(m_nUpperLeftX, m_nUpperLeftY + i * m_nColorHeight, m_nColorWidth, m_nColorHeight );
			canvas.setColor( Color.black );
			canvas.drawRect(m_nUpperLeftX, m_nUpperLeftY + i * m_nColorHeight, m_nColorWidth, m_nColorHeight );
			
			m_nLowerRightX = m_nUpperLeftX + m_nColorWidth;
			m_nLowerRightY = ( i + 1 ) * m_nColorHeight;
		}
		
	}
	
	void DrawTestShape( Graphics canvas )
	{
		canvas.drawImage(m_objShape, m_nTestShapeX, m_nTestShapeY, null);
	}
	
	void SetPixel( int x, int y, Graphics canvas )
	{
		canvas.drawLine(x, y, x, y);
	}
	
	void SetPixel( int x, int y, int nColor )
	{
		m_objShape.setRGB(x, y, nColor);
	}
	
	public int GetPixel( int x, int y )
	{
		return( m_objShape.getRGB(x, y) );
	}
	
	public void paint( Graphics canvas )
	{
		DrawColors( canvas );
		DrawTestShape( canvas );
	}
	
	void DoRecursiveFill( int x, int y )
	{
		x -= m_nTestShapeX;
		y -= m_nTestShapeY;
		m_nStartColor = GetPixel(x,y) | 0xff000000;
		Graphics canvas = getGraphics();
		canvas.setColor( m_objSelectedColor );
		int w = m_objShape.getWidth();
		int h = m_objShape.getHeight();

		if( m_nStartColor == m_nSelectedColor)
		{
			return;
		}
		
		RecursiveFill( x, y, w, h, canvas);
	}
	
	void RecursiveFill( int x, int y, int w, int h, Graphics canvas )
	{
		
		if(y<0 || x<0 || x>=w) return;
		if(GetPixel(x,y) != m_nStartColor) return;
		
		SetPixel(x+m_nTestShapeX, y+m_nTestShapeY, canvas);
		SetPixel(x,y,m_nSelectedColor);
	
		RecursiveFill( x-1, y, w, h, canvas);
		RecursiveFill( x+1, y, w, h, canvas);
		RecursiveFill( x, y-1, w, h, canvas);
		RecursiveFill( x, y+1, w, h, canvas);
		
	}
	
	int m_nStartX, m_nStartY, m_nStartColor;
	void DoFloodFill( int x, int y )
	{
		x -= m_nTestShapeX;
		y -= m_nTestShapeY;
		m_nStartColor = GetPixel(x,y) | 0xff000000;
		Graphics canvas = getGraphics();
		canvas.setColor( m_objSelectedColor );
		int w = m_objShape.getWidth();
		int h = m_objShape.getHeight();

		if( m_nStartColor == m_nSelectedColor)
		{
			return;
		}
		
		FloodFill( x, y, w, h, canvas);
	}
	
	void FloodFill( int x, int y, int w, int h, Graphics canvas )
	{
		// base case (boundary of image)
				if (y < 0 || y > m_objShape.getHeight()-1)
					return;
				
				// base case (pixel clicked is same color as selected)
				if (targetColor == m_objSelectedColor.getRGB())
					return;
				
				// base case (pixel passed is already selected color)
				if (m_objSelectedColor.getRGB() == GetPixel(x, y))
					return;
				
				// index to traverse left
				int left = x;
				// iterate left changing only target pixels
				while ( GetPixel(left, y) == targetColor )
				{
					// change the current pixel 
					SetPixel(left, y, m_objSelectedColor.getRGB());
					left--;
					// boundary
					if (left < 0)
						break;
				}
				
				// index to traverse right
				int right = x+1;
				// iterate right changing only target pixels
				while ( GetPixel(right, y) == targetColor )
				{
					// change the current pixel
					SetPixel(right, y, m_objSelectedColor.getRGB());
					right++;
					// boundary
					if (right > m_objShape.getWidth()-1)
						break;
				}
				
				// iterate over row for recursive calls
				for (left += 1; left < right ; left++)
				{
					// call up
					FloodFill(left,y+1,w,h,canvas);
					//call down
					FloodFill(left,y-1,w,h,canvas);
				}
				// make sure we get the picture
				repaint();
	}
	@Override
	public void mouseClicked(MouseEvent ms) 
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
	}

	@Override
	public void mousePressed(MouseEvent ms) 
	{
		if( ms.getX() >= m_nUpperLeftX &&
				ms.getY() >= m_nUpperLeftY &&
				ms.getX() < m_nLowerRightX &&
				ms.getY() < m_nLowerRightY )
			{
				int nColorIndex = ( ms.getY() - m_nUpperLeftY ) / m_nColorHeight;
				if( nColorIndex >= 0 && nColorIndex <= 7 )
				{
					m_objSelectedColor = m_Colors[nColorIndex];
					m_nSelectedColor = m_Colors[nColorIndex].getRGB();
				}
			}
			
			else if( ms.getX() >= m_nTestShapeX &&
				ms.getY()>=m_nTestShapeY &&
				ms.getX() < m_nTestShapeX + m_objShape.getWidth() &&
				ms.getY() < m_nTestShapeY + m_objShape.getHeight())
			{
				if( full.getState() )
				{
					DoRecursiveFill( ms.getX(), ms.getY());
				}
				else
				{
					targetColor = GetPixel(ms.getX()-100, ms.getY()-100);
					DoFloodFill( ms.getX(), ms.getY());
				}
			}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
	}
	
}
