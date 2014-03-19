
package org.omelogic.hocuslocus.wdjet.client;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.distortion.Distortion;
import prefuse.action.distortion.FisheyeDistortion;
import prefuse.action.layout.Layout;
import prefuse.controls.AnchorUpdateControl;
import prefuse.controls.ControlAdapter;
import prefuse.data.Tuple;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.tuple.*;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.controls.FocusControl;
import prefuse.data.event.TupleSetListener;


import org.omelogic.hocuslocus.logic.*;

/**
 * <p>A prefuse-based implementation of Fisheye Menus, showcasing the use of
 * visual distortion to provide access to a large number of data items
 * without scrolling.</p>
 * 
 * <p>This implementation is inspired by the Fisheye Menu research conducted
 * by Ben Bederson at the University of Maryland. See the
 * <a href="http://www.cs.umd.edu/hcil/DataMenu/">Fisheye Menu project
 * web site</a> for more details.</p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class OmeLogicListView extends Display {

	private static final String ITEMS = "items";
    private Table m_items = OmeLogicView.OMENODE_SCHEMA.instantiate(); 
    // table of menu items
    
    private double m_scale = 7;       // scale parameter for fisheye distortion
	private double m_maxHeight =600;
   
    /**
     * Create a new, empty DataMenu.
     * @see #addMenuItem(String, javax.swing.Action)
     */
    public OmeLogicListView( Table nodeTable , String label) {
        
        super(new Visualization());
      	m_items = nodeTable;
        m_vis.addTable(ITEMS, m_items);
        
        // set up the renderer to use
        LabelRenderer renderer = new LabelRenderer(OmeLogicView.LABEL);
        renderer.setHorizontalPadding(0);
        renderer.setVerticalPadding(1);
        renderer.setHorizontalAlignment(Constants.LEFT);
        m_vis.setRendererFactory(new DefaultRendererFactory(renderer));
        
        // set up this display
        setSize(100,500);
        setHighQuality(true);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,5));
        addControlListener(new ControlAdapter() {
            // dispatch an action event to the menu item
            public void itemClicked(VisualItem item, MouseEvent e) {
                ActionListener al = (ActionListener)item.get(OmeLogicView.ACTION);
                al.actionPerformed(new ActionEvent(item, e.getID(),
                    "click", e.getWhen(), e.getModifiers()));
            }
        });
                // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                {
                    //((VisualItem)rem[i]).setFixed(false);
                    ((VisualItem)rem[i]).setHighlighted(false);
                    ((VisualItem)rem[i]).set(OmeLogicView.SELECTED, false);
                    
                }
                for ( int i=0; i<add.length; ++i ) {
                    //((VisualItem)add[i]).setFixed(false);
                    //((VisualItem)add[i]).setFixed(true);
                    ((VisualItem)add[i]).setHighlighted(true);
                    ((VisualItem)add[i]).set(OmeLogicView.SELECTED, true);
                }
                if ( ts.getTupleCount() == 0 ) {
                    //ts.addTuple(rem[0]);
                    //((VisualItem)rem[0]).setFixed(false);
                }
                m_vis.run("draw");
            }
        });
        
        // text color function
        // items with the mouse over printed in red, otherwise black
        ColorAction colors = new ColorAction(ITEMS, VisualItem.TEXTCOLOR);
        colors.setDefaultColor(ColorLib.gray(0));
        colors.add("hover()", ColorLib.rgb(255,0,0));
        colors.add(OmeLogicView.SELECTED, ColorLib.rgb(255,0,0));
        
        // initial layout and coloring
        ActionList init = new ActionList();
        init.add(new VerticalLineLayout(m_maxHeight));
        init.add(colors);
        init.add(new RepaintAction());
        m_vis.putAction("init", init);

        // fisheye distortion based on the current anchor location
        ActionList distort = new ActionList();
        Distortion feye = new FisheyeDistortion(m_scale);
        distort.add(feye);
        distort.add(colors);
        distort.add(new RepaintAction());
        m_vis.putAction("distort", distort);
        
        // update the distortion anchor position to be the current
        // location of the mouse pointer
        addControlListener(new AnchorUpdateControl(feye, "distort"));
        addControlListener(new FocusControl(1));

		this.getVisualization().run("init");
        //this.update(nodeTable);
        
    }
    
    
    public void update( Table nodeTable )
    {
    	m_vis.removeGroup(ITEMS);
    	m_items = nodeTable;
    	m_vis.addTable(ITEMS, m_items);
   		this.getVisualization().run("init");
    					
    	
    }
    
    /**
     * Lines up all VisualItems vertically. Also scales the size such that
     * all items fit within the maximum layout size, and updates the
     * Display to the final computed size.
     */
    public class VerticalLineLayout extends Layout {
        private double m_maxHeight = 600;
        
        public VerticalLineLayout(double maxHeight) {
            m_maxHeight = maxHeight;
        }
        
        public void run(double frac) {
            // first pass
            double w = 0, h = 0;
            Iterator iter = m_vis.items();
            while ( iter.hasNext() ) {
                VisualItem item = (VisualItem)iter.next();
                item.setSize(1.0);
                h += item.getBounds().getHeight();
            }
            double scale = h > m_maxHeight ? m_maxHeight/h : 1.0;
            
            Display d = m_vis.getDisplay(0);
            Insets ins = d.getInsets();
            
            // second pass
            h = ins.top;
            double ih, y=0, x=ins.left;
            iter = m_vis.items();
            while ( iter.hasNext() ) {
                VisualItem item = (VisualItem)iter.next();
                item.setSize(scale); item.setEndSize(scale);
                Rectangle2D b = item.getBounds();
                
                w = Math.max(w, b.getWidth());
                ih = b.getHeight();
                y = h+(ih/2);
                setX(item, null, x);
                setY(item, null, y);
                h += ih;
            }
            
            // set the display size to fit text
            d.setSize((int)Math.round(2*m_scale*w + ins.left + ins.right),
                      (int)Math.round(h + ins.bottom));
        }
    } // end of inner class VerticalLineLayout
    
} // end of class DataMenu
