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
import prefuse.action.layout.Layout;
import prefuse.controls.AnchorUpdateControl;
import prefuse.controls.ControlAdapter;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.SizeAction;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.activity.ActivityAdapter;
import prefuse.activity.ActivityListener;
import prefuse.controls.ControlAdapter;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.util.ui.BrowserLauncher;
import prefuse.visual.VisualItem;
import prefuse.visual.sort.ItemSorter;

public class ActionMenuNodeAction extends AbstractAction
{
		public ActionMenuNodeAction(){
			super();
		}
		public void actionPerformed(ActionEvent e) {
			System.out.println("clicked item: "+
				((VisualItem)e.getSource()).get(ActionMenu.LABEL));
			System.out.flush();
		}
}    		
