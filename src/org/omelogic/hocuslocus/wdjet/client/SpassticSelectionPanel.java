/*
 *      SpassticSelectionPanel.java
 *      
 *      Copyright 2008 Ajish George <ajish@hocuslocus.com>
 *      
 */




package org.omelogic.hocuslocus.wdjet.client;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.Exception;
import java.util.*;

import org.omelogic.hldbmanager.*;
import org.omelogic.utils.locussetio.*;
import org.omelogic.utils.*;
import org.omelogic.locus.*;
import org.omelogic.locus.filter.*;
import org.omelogic.interlocutor.*;

public class SpassticSelectionPanel extends JPanel implements ListSelectionListener
{

	TableDescriptor[] tables;
	JList m_list;
	JTextArea description;
	boolean[] tableSelection;
	int numSelected;
	int curHighlighted;
	int prevHighlighted;
	
	Locution[] locuParams;
	JComboBox liaCompTypeBox;
	JComboBox liaCompStrandBox;
	JTextField liaCompValueBox;
	JCheckBox flatQueryBox;
	
	JList qFilterListBox;
	JList tFilterListBox;
	
	boolean isSimple;
	
	//LocusSieveListModel[] qFilterListModels;
	//LocusSieveListModel[] tFilterListModels;

	//list of named checkboxes
	//when an item in the list is selected,
	//a description is shown to the right
	
	
	public SpassticSelectionPanel(WdjetService service, String spass, boolean simple) throws Exception 
	{
	
		super();
		
		try
		{//gets data to populate choices
			tables = service.getTableDescriptors(spass);
			tableSelection = new boolean[tables.length];
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,"WdjetService.getTableDescriptors() failed while making SpassticSelectionPanel!");
			throw new Exception("WdjetService.getTableDescriptors() failed while making SpassticSelectionPanel!\n" + e.toString());
		}
		
		isSimple = simple;
		
		
		numSelected = 0;
		curHighlighted = -1;
		prevHighlighted = -1;
		
		m_list = new JList(tables);
		SpassticCheckListCellRenderer renderer = new SpassticCheckListCellRenderer();
		m_list.setCellRenderer(renderer);
		m_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		SpassticCheckListener lst = new SpassticCheckListener(this);
		m_list.addMouseListener(lst);
		m_list.addKeyListener(lst);

		JScrollPane ps = new JScrollPane();
		ps.getViewport().add(m_list);

		description = new JTextArea(5,25);
		description.setEditable(false);
		description.setLineWrap(true);
		description.setBorder(new TitledBorder(new EtchedBorder(), 
			"Description:") );

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.add(ps, BorderLayout.WEST);
		top.add(description, BorderLayout.EAST);
		top.setBorder( BorderFactory.createTitledBorder("Available Tables"));

		
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());
		all.add(top, BorderLayout.NORTH);
		if (!isSimple){
			all.add(makeParametersPanel(), BorderLayout.SOUTH);
		}
		this.add(all);
		
	}
	
	
	private JPanel makeParametersPanel()
	{
				
		locuParams = new Locution[ tables.length ];
		for ( int i = 0; i < locuParams.length; i++ ){
			locuParams[i] = new Locution();
		}

		m_list.addListSelectionListener(this);

		liaCompStrandBox = new JComboBox( Locus.COMPARISON_STRAND_DESCRIPTIONS );
		//liaCompStrandBox.setBorder( BorderFactory.createTitledBorder("Comparison Strand"));
		liaCompTypeBox = new JComboBox( Locus.COMPARISON_TYPE_DESCRIPTIONS );
		//liaCompTypeBox.setBorder( BorderFactory.createTitledBorder("Comparison Type"));
		liaCompValueBox = new JTextField( 10 );
		//liaCompValueBox.setBorder( BorderFactory.createTitledBorder("Comparison Value"));
		flatQueryBox = new JCheckBox("Flatten Query Set?");
		

		JPanel locutionLIAPanelTop = new JPanel();
		//locutionPanelTop.add(new JLabel("Flatten QuerySet?"));
		locutionLIAPanelTop.add(flatQueryBox);
		locutionLIAPanelTop.add(new JLabel("Comparison Strand"));
		locutionLIAPanelTop.add(liaCompStrandBox);
		JPanel locutionLIAPanelBottom = new JPanel();
		locutionLIAPanelBottom.add(new JLabel("Comparison Type"));
		locutionLIAPanelBottom.add(liaCompTypeBox);
		locutionLIAPanelBottom.add(new JLabel("Comparison Value"));
		locutionLIAPanelBottom.add(liaCompValueBox);
		JPanel locutionLIAPanel = new JPanel();
		locutionLIAPanel.setLayout(new BoxLayout(locutionLIAPanel, BoxLayout.PAGE_AXIS));
		locutionLIAPanel.add(locutionLIAPanelTop);
		locutionLIAPanel.add(locutionLIAPanelBottom);
		//locutionLIAPanel.setBorder( BorderFactory.createTitledBorder("Intersection Parameters"));

		qFilterListBox = new JList();
		qFilterListBox.setVisibleRowCount(4);
		qFilterListBox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		qFilterListBox.setLayoutOrientation(JList.VERTICAL);
		JScrollPane qFilterListScroller = new JScrollPane(qFilterListBox);
		//qFilterListScroller.setPreferredSize( new Dimension(100,50) );
		JButton addqFilterButton = new JButton("Add Filter");
		JButton remqFilterButton = new JButton("Remove Filter");
		addqFilterButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if (!(curHighlighted >=0)){
					return;
				}
				LocusSieve newFilter = (new LocusSieveDialog()).getSieve();
				if (newFilter == null){
					return;
				}
				locuParams[curHighlighted].getQuerySieveList().add(newFilter);
				qFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getQuerySieveList() ));
			}
		});
		remqFilterButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if (!(curHighlighted >=0)){
					return;
				}
				int filterIndex = qFilterListBox.getSelectedIndex();
				if (filterIndex >= 0){
					locuParams[curHighlighted].getQuerySieveList().remove(filterIndex);
					qFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getQuerySieveList() ));
				}
			}
		});

		JPanel locutionFilterPanelLeft = new JPanel();
		locutionFilterPanelLeft.setLayout(new BorderLayout());
		locutionFilterPanelLeft.add(new JLabel("Query Filters"), BorderLayout.NORTH);
		locutionFilterPanelLeft.add(qFilterListScroller, BorderLayout.CENTER);
		JPanel locutionFilterLeftButtons = new JPanel();
		locutionFilterLeftButtons.add(addqFilterButton);
		locutionFilterLeftButtons.add(remqFilterButton);
		locutionFilterPanelLeft.add(locutionFilterLeftButtons, BorderLayout.SOUTH);


		tFilterListBox = new JList();
		tFilterListBox.setVisibleRowCount(4);
		tFilterListBox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		tFilterListBox.setLayoutOrientation(JList.VERTICAL);
		JScrollPane tFilterListScroller = new JScrollPane(tFilterListBox);
		tFilterListScroller.setPreferredSize( new Dimension(100,50) );
		JButton addtFilterButton = new JButton("Add Filter");
		JButton remtFilterButton = new JButton("Remove Filter");
		addtFilterButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if (!(curHighlighted >=0)){
					return;
				}
				LocusSieve newFilter = (new LocusSieveDialog()).getSieve();
				if (newFilter == null){
					return;
				}
				locuParams[curHighlighted].getTargetSieveList().add(newFilter);
				tFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getTargetSieveList() ));
			}
		});
		remtFilterButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				if (!(curHighlighted >=0)){
					return;
				}
				int filterIndex = tFilterListBox.getSelectedIndex();
				if (filterIndex >= 0){
					locuParams[curHighlighted].getTargetSieveList().remove(filterIndex);
					tFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getTargetSieveList() ));
				}
			}
		});
		
		JPanel locutionFilterPanelRight = new JPanel();
		locutionFilterPanelRight.setLayout(new BorderLayout());
		locutionFilterPanelRight.add(new JLabel("Target Filters"), BorderLayout.NORTH);
		locutionFilterPanelRight.add(tFilterListScroller, BorderLayout.CENTER);
		JPanel locutionFilterRightButtons = new JPanel();
		locutionFilterRightButtons.add(addtFilterButton);
		locutionFilterRightButtons.add(remtFilterButton);
		locutionFilterPanelRight.add(locutionFilterRightButtons, BorderLayout.SOUTH);
		
		JPanel locutionFilterPanel = new JPanel();
		locutionFilterPanel.setLayout(new BorderLayout());
		locutionFilterPanel.add(locutionFilterPanelLeft, BorderLayout.WEST);
		locutionFilterPanel.add(locutionFilterPanelRight, BorderLayout.EAST);
		locutionFilterPanel.setBorder( BorderFactory.createTitledBorder("Intersection Filters"));

		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		bottom.add(locutionLIAPanel, BorderLayout.NORTH);
		bottom.add(locutionFilterPanel, BorderLayout.SOUTH);	
		
		return bottom;
	}
	
	protected void addListSelectionListener( ListSelectionListener listen )
	{
		m_list.addListSelectionListener( listen );
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		float liaCompValue;
		
		if (curHighlighted != -1){
			//save all the values here to the locution item
			try{
				liaCompValue = Float.parseFloat(liaCompValueBox.getText());
			}catch(Exception xcep){
				m_list.setSelectedIndex(curHighlighted);
				JOptionPane.showMessageDialog(this,"LIA Comparison Value - Not a number!");
				return;
			}
			System.out.println("Parsed: " + liaCompValue);
			if (liaCompTypeBox.getSelectedIndex() == Locus.COMPARISON_TYPE.PERCENT )
			{
				liaCompValue = liaCompValue / 100;
				if ( liaCompValue < 0 || liaCompValue > 1){
					m_list.setSelectedIndex(curHighlighted);
					System.out.println("Percent is "+ liaCompValue+"!");
					JOptionPane.showMessageDialog(this,"LIA Comparison Value - PERCENT must be between 0 and 100 percent!");
					return;
				}
			}else{
				if ( liaCompValue != (float)Math.round(liaCompValue)){
					m_list.setSelectedIndex(curHighlighted);
					
					JOptionPane.showMessageDialog(this,"LIA Comparison Value - FIXED must be a positive or negative integer!");
					return;
				}
			}
			locuParams[curHighlighted].setComparisonStrand( liaCompStrandBox.getSelectedIndex() );
			locuParams[curHighlighted].setComparisonType( liaCompTypeBox.getSelectedIndex() );
			locuParams[curHighlighted].setComparisonValue( liaCompValue );
			locuParams[curHighlighted].setFlattenQuery( flatQueryBox.isSelected() );
			
			
		}
		
		curHighlighted = m_list.getSelectedIndex();		
		
		liaCompStrandBox.setSelectedIndex( locuParams[curHighlighted].getComparisonStrand() );
		liaCompTypeBox.setSelectedIndex( locuParams[curHighlighted].getComparisonType() );
		liaCompValue = locuParams[curHighlighted].getComparisonValue();
		if (locuParams[curHighlighted].getComparisonType() == Locus.COMPARISON_TYPE.PERCENT){
			liaCompValue = liaCompValue *100;
		}
		liaCompValueBox.setText( Float.toString(liaCompValue) );
		flatQueryBox.setSelected( locuParams[curHighlighted].getFlattenQuery() );
		
		qFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getQuerySieveList() ));
		tFilterListBox.setModel(new LocusSieveListModel( locuParams[curHighlighted].getTargetSieveList() ));
	}
	
	
	protected int getNumTargets()
	{
		return tables.length;
	}
	
	protected int getCurrentIndex()
	{
		return curHighlighted;
	}
	
	protected TableDescriptor getTableByIndex(int i)
	{
		return tables[i];
	}

	protected int[] getSelectedIndices()
	{
		int[] outSelected = new int[numSelected];
		System.out.println("Adding tables to selection:\n");
		
		int k=0;
		for (int i = 0; i < tables.length; i++)
		{
			if (tableSelection[i]){
				outSelected[k++] = i;
			}
		}
		
		return outSelected;
	}
	
		
	
	protected ArrayList<TableDescriptor> getSelectedTables()
	{
		if (!isSimple){
			this.valueChanged(null);
		}
		ArrayList<TableDescriptor> outSelected = new ArrayList<TableDescriptor>();
		System.out.println("Adding tables to selection:\n");
		
		for (int i = 0; i < tables.length; i++)
		{
			if (tableSelection[i])
			{
				System.out.println(i + "--" + tables[i].getFriendlyName());
				outSelected.add(tables[i]);
			}
		}
		
		return outSelected;
	}
	
	protected ArrayList<Locution> getSelectedLocutions()
	{
		this.valueChanged(null);
		ArrayList<Locution> outSelected = new ArrayList<Locution>();
		System.out.println("Adding tables to selection:\n");
		
		for (int i = 0; i < tables.length; i++)
		{
			if (tableSelection[i])
			{
				System.out.println(i + "--" + tables[i].getFriendlyName());
				outSelected.add(locuParams[i]);
			}
		}
		
		return outSelected;
	}
	
	protected void invertSelection( int index )
	{
		if (tableSelection[index]==true)
		{
			tableSelection[index]=false;
			numSelected--;
		}else{
			tableSelection[index]=true;
			numSelected++;
		}
	}
	
	class LocusSieveListModel implements ListModel
	{
		private java.util.List<LocusSieve> managedList;
		
		public LocusSieveListModel( java.util.List<LocusSieve> sieveList )
		{
			managedList = sieveList;
		}
		
		public Object getElementAt(int index)
		{
			return managedList.get(index);
		}
		
		public int getSize() 
		{
			return managedList.size();
		}
		
		public void addListDataListener(ListDataListener l) {}
		
		public void removeListDataListener(ListDataListener l){}
	}
		
	
	class SpassticCheckListCellRenderer extends JCheckBox implements ListCellRenderer
	{
		protected Border m_noFocusBorder = 
			new EmptyBorder(1, 1, 1, 1);

		public SpassticCheckListCellRenderer()
		{
			super();
			setOpaque(true);
			setBorder(m_noFocusBorder);
		}

		public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			

			setBackground(isSelected ? list.getSelectionBackground() : 
				list.getBackground());
			setForeground(isSelected ? list.getSelectionForeground() : 
				list.getForeground());

			TableDescriptor tdesc = (TableDescriptor)value;
			setSelected(tableSelection[index]);
			if (cellHasFocus)
			{
				description.setText(tdesc.getDescription());
			}
			
			setText(tdesc.getFriendlyName());

			setFont(list.getFont());
			setBorder((cellHasFocus) ? 
				UIManager.getBorder("List.focusCellHighlightBorder")
				 : m_noFocusBorder);

			return this;
		}
	}

	class SpassticCheckListener implements MouseListener, KeyListener
	{
		protected SpassticSelectionPanel m_parent;
		protected JList m_list;

		public SpassticCheckListener(SpassticSelectionPanel parent)
		{
			m_parent = parent;
			m_list = parent.m_list;
		}

		public void mouseClicked(MouseEvent e)
		{
			if (e.getX() < 20)
				doCheck();
		}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void keyPressed(KeyEvent e)
		{
			if (e.getKeyChar() == ' ')
				doCheck();
		}

		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}

		protected void doCheck()
		{
			int index = m_list.getSelectedIndex();
			if (index < 0)
				return;
			m_parent.invertSelection(index);
			m_list.repaint();
		}
	}	
	

}

