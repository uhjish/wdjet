package org.omelogic.hocuslocus.wdjet.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.tuple.*;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import org.omelogic.hocuslocus.logic.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class OmeLogicGraphView extends JPanel {

    private static final String GRAPH = "graph";
    private static final String NODES = "graph.nodes";
    private static final String EDGES = "graph.edges";

    private Visualization m_vis;
    

    public OmeLogicGraphView(Graph graph, String label) 
    {
        
        // create a new, empty visualization for our data
        m_vis = new Visualization();
        
        // --------------------------------------------------------------------
        // set up the renderers
        
        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization
        
        // adds graph to visualization and sets renderer label field
        setGraph(graph, label);
        
        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                {
                    ((VisualItem)rem[i]).setFixed(false);
                    ((VisualItem)rem[i]).set(OmeLogicView.SELECTED, false);
                }
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(true);
                    ((VisualItem)add[i]).set(OmeLogicView.SELECTED, true);
                }
                if ( ts.getTupleCount() == 0 ) {
                    //ts.addTuple(rem[0]);
                    //((VisualItem)rem[0]).setFixed(false);
                    //((VisualItem)rem[i]).set(OmeLogicView.SELECTED, false);
                }
                m_vis.run("draw");
            }
        });
        
        
        
        // --------------------------------------------------------------------
        // create actions to process the visual data


        ColorAction fill = new ColorAction(NODES, 
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        //fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
        fill.add(OmeLogicView.SELECTED, ColorLib.rgb(255,100,100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));
        
        ActionList draw = new ActionList();
        draw.add(fill);
        draw.add(new ColorAction(NODES, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction(EDGES, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(EDGES, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(GRAPH, true));
        animate.add(fill);
        animate.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");
        
        
        // --------------------------------------------------------------------
        // set up a display to show the visualization
        
        Display display = new Display(m_vis);
        display.setSize(700,500);
        display.pan(350, 350);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        
        // main display controls
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());

        // overview display
        Display overview = new Display(m_vis);
        overview.setSize(700,500);
        overview.addItemBoundsListener(new FitOverviewListener());
        
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
                
        // now we run our action list
        m_vis.run("draw");
        
        this.add(display);
        //this.setVisible(true);
    }
    
    public void setGraph(Graph g, String label) {
        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory)
                                                m_vis.getRendererFactory();
        ((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);
        
        // update graph
        m_vis.removeGroup(GRAPH);
        VisualGraph vg = m_vis.addGraph(GRAPH, g);
        m_vis.setValue(EDGES, null, VisualItem.INTERACTIVE, Boolean.TRUE);
        if (vg.getNodeCount() > 0)
        {
			VisualItem f = (VisualItem)vg.getNode(0);
			m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
			f.setFixed(false);
        	
        }
        
    }
    
    
    public static class FitOverviewListener implements ItemBoundsListener {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;
        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25/d.getScale());
            
            double dd = m_d/d.getScale();
            double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
            if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }
    
} // end of class OmeLogicGraphView
