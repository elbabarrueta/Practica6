package Swing;

import javax.swing.*;


import javax.swing.event.*;

import javax.swing.table.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Ventana extends JFrame{
    private JLabel messageLabel;
    private JPanel panelVisualizacion;
    private JPanel panelbotones;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton orderButton;
    
    private MiJTree municipioTree;
    private DefaultTreeModel modeloArbol;	
	private DefaultMutableTreeNode raiz;
	private DataSetMunicipios datos;
	
	private JTable municipioTable;
	private MiTableModel modeloDatos;
	
	private int Orden;
	
//PASO 7
	private Color redColor = Color.RED;
	private Color greenColor = Color.GREEN;

    public Ventana(JFrame ventOrigen) {
//TAREA 2
        new JFrame("Municipios Españoles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        messageLabel = new JLabel();
        add(messageLabel, BorderLayout.NORTH);
        
        municipioTree = new MiJTree();		
        //municipioTree = new JTree(modeloArbol);
        //municipioTree.setRootVisible(true);
        JScrollPane treeScrollPane = new JScrollPane(municipioTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 400));
        add(treeScrollPane, BorderLayout.WEST);

        municipioTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(municipioTable);
        tableScrollPane.setPreferredSize(new Dimension(200, 400));
        add(tableScrollPane, BorderLayout.CENTER);

        panelVisualizacion = new JPanel() {
        	@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Grafico((Graphics2D) g);
                {
                    setOpaque(true);
                    setPreferredSize(new Dimension(300, 600));
                }
                panelVisualizacion.repaint();
            }
        };
        this.add(panelVisualizacion, BorderLayout.EAST);

        insertButton = new JButton("Inserción");
        deleteButton = new JButton("Borrado");
        orderButton = new JButton("Orden");
        panelbotones = new JPanel();
        panelbotones.add(insertButton);
        panelbotones.add(deleteButton);
        panelbotones.add(orderButton);
        add(panelbotones, BorderLayout.SOUTH);
      
        
    // PASO 8
        insertButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) municipioTree.getSelectionPath().getLastPathComponent();
				if(nodo != null && nodo.isLeaf()) {
					String provSelec = (String) nodo.getUserObject();
					String autoSelec = (String) nodo.getParent().toString();
					Municipio nuevoM = new Municipio(0, "", 50000, provSelec, autoSelec);
					datos.anyadir(nuevoM);
				//	int fila = modeloDatos.getRowCount() +1;
				//	((MiTableModel) modeloDatos).borraFila(fila);
					((MiTableModel)modeloDatos).setListaMunicipios(datos.MunicipiosPorProvincia(provSelec));
				//	System.out.println();
				//	datos.setlMunicipios(datos.MunicipiosPorProvincia(provSelec));
					municipioTable.repaint();
					
				}
			}
		});
    // PASO 9
        deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int seleccionado = municipioTable.getSelectedRow();
				if( seleccionado >= 0) {
					String provinS = (String) municipioTable.getValueAt(seleccionado, 3);
					int texto = JOptionPane.showConfirmDialog(Ventana.this, "¿Quieres eleminar este municipio?", "Confirmacion", JOptionPane.YES_NO_OPTION);
					if(texto == JOptionPane.YES_OPTION ) {
						((MiTableModel) modeloDatos).borraFila(seleccionado);
						((MiTableModel)modeloDatos).setListaMunicipios(datos.MunicipiosPorProvincia(provinS));
						municipioTable.setModel(modeloDatos);
						municipioTable.repaint();
						
					}
				}else {
					JOptionPane.showMessageDialog(null, "Elige un municipio para poder eliminarlo.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
        
    // PASO 10    	
        orderButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String prov = municipioTree.getSelectionPath().getLastPathComponent().toString();
				System.out.println(prov);
			
				List<Municipio> municipiosEnProvincia = datos.MunicipiosPorProvincia(prov);

				if(Orden == 1) {
				municipiosEnProvincia.sort(Comparator.comparing(Municipio::getNombre));
				Orden = 0;

				}else {
				municipiosEnProvincia.sort(Comparator.comparingInt(Municipio::getHabitantes).reversed());
				Orden = 1;
				}
				((MiTableModel) municipioTable.getModel()).setListaMunicipios(municipiosEnProvincia);
				municipioTable.repaint();
			}
		});
         
        
        setVisible(true);
    }
    
      
    
//TAREA 3
    public void setDatos(DataSetMunicipios datos) {
    	this.datos = datos;
    	
		raiz = new DefaultMutableTreeNode("Municipios");
		modeloArbol = new DefaultTreeModel(raiz);		
		municipioTree.setModel(modeloArbol);
		municipioTree.setEditable(false);
		
		List<Municipio> listaMunicipios = datos.getListaMunicipios();
		crearNodos(raiz, listaMunicipios);
		
		//TAREA 11
		rendererProvin(municipioTree, datos);
		
		
//TAREA 4
		 municipioTree.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreePath tp = e.getPath();
					if(tp != null) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) municipioTree.getLastSelectedPathComponent();
			        	if (selectedNode != null && selectedNode.isLeaf()) {
			        		// Obtén el nombre de la provincia seleccionada
			        		cargarTabla((String) selectedNode.getUserObject());
			        		
			        		rendererProvin(municipioTree, datos);
			        	}
					}	
				}
			});
	}

//TAREA 11
    private void rendererProvin(JTree municipioTree, DataSetMunicipios datos) {
    	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    	municipioTree.setCellRenderer(new DefaultTreeCellRenderer() {

    		
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if(value instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	                Object userObject = node.getUserObject();
	                
	                if(userObject != null && node.isLeaf()) {
	                	String provincia = (String) userObject;
	                	int totalHabitantes = datos.HabitantesPorProvincia(provincia);
	                	JProgressBar progressBar = new JProgressBar(0, 5000000);
	                	progressBar.setValue(totalHabitantes);
	                
	                    JPanel panel = new JPanel(new BorderLayout());
	                    panel.add(this, BorderLayout.WEST);
	                    panel.add(progressBar, BorderLayout.EAST);
	                    return panel;  
	                }
				}
				return this;	
			}
    		
    	});
    }

  //TAREA 12
    private void Grafico(Graphics2D grafico) {
    	int altoPD = panelVisualizacion.getHeight();
    	int anchoPD = panelVisualizacion.getWidth();
    	if(municipioTree.getSelectionPath() != null) {
    		DefaultMutableTreeNode nodoseleccionado = (DefaultMutableTreeNode) municipioTree.getSelectionPath().getLastPathComponent();
    		String provincia = "";
    		if(nodoseleccionado != null && nodoseleccionado.isLeaf()) {
    			provincia = (String) nodoseleccionado.getUserObject();
    		}
    		List<Municipio> m = datos.MunicipiosPorProvincia(provincia);
    		int PersonasProv = 0;
    		int Total = 0;
    		
    		for(Municipio mun: m) {
    			PersonasProv += mun.getHabitantes();
    		}
    		for(Municipio mun: datos.getListaMunicipios()) {
    			Total += mun.getHabitantes();
    		}
    		
    		int altoT = altoPD - 40;
    		int AnchoBP = (anchoPD / 2) - 20;
    		//int altoT = altoPD - 40;
    		double porProvincia = (double)PersonasProv/Total;
    		int AltoBP = (int)(porProvincia*altoT);
    		int xBarraP = 10;
    		int yBarraP = altoPD - AltoBP;
    		
    		grafico.setColor(Color.GRAY);
    		grafico.fillRect(xBarraP, yBarraP, anchoPD, altoPD);
    		
    		grafico.setColor(Color.BLACK);
    		int ySep = yBarraP;
    		for(Municipio mun: m) {
    			int altoLinea = (int)((double)mun.getHabitantes()/PersonasProv*AltoBP);
    			grafico.drawLine(xBarraP, ySep, xBarraP+AltoBP, ySep);
    			grafico.drawLine(xBarraP, ySep+altoLinea, xBarraP+AnchoBP, ySep+altoLinea);
    			ySep += altoLinea;
    		}
    		 int anchoBarraEstado = (anchoPD / 2) - 20;
    	     int xBarraEstado = xBarraP + AnchoBP + 10;
    	     int alturaBarraEstado = altoT;
    	     int yBarraEstado = altoPD - alturaBarraEstado;

    	     grafico.setColor(Color.ORANGE);
    	     grafico.fillRect(xBarraEstado, yBarraEstado, anchoBarraEstado, alturaBarraEstado);

    	     grafico.setColor(Color.BLACK); 
    	     
    	     
    	}
    }

    
//TAREA 3
    private DefaultMutableTreeNode crearNodo(Object dato, DefaultMutableTreeNode nodoPadre, int posicion) {
		DefaultMutableTreeNode nodo1 = new DefaultMutableTreeNode(dato);	
		modeloArbol.insertNodeInto(nodo1, nodoPadre, posicion);	
		municipioTree.expandir(new TreePath(nodo1.getPath()), true);	
		return nodo1;
	}
    
    
    private void crearNodos(DefaultMutableTreeNode nodoPadre, List<Municipio> listaMunis) {
    	ArrayList<String> comunidadAu = new ArrayList<String>();
    	for( Municipio m : listaMunis) {
    		String Autonomia = m.getAutonomia();
    		if(!comunidadAu.contains(Autonomia)) {
    			DefaultMutableTreeNode nodoComuni = crearNodo(Autonomia, nodoPadre, comunidadAu.size());
    			comunidadAu.add(Autonomia);
    			
    			ArrayList<String> provincias = new ArrayList<String>();
    			for(Municipio muni: listaMunis) {
    				if(muni.getAutonomia().equals(Autonomia)) {
    					String provinvia = muni.getProvincia();
    					if(!provincias.contains(provinvia)) {
    						crearNodo(provinvia, nodoComuni, provincias.size());
    						provincias.add(provinvia);
    					}
    				}
    			}
    			
    		}
    	}
    	
    }
    
    public static class MiJTree extends JTree{
		public void expandir(TreePath path, boolean estado) {	//expandir el camino del nodo
			setExpandedState( path, estado );	//metdodo a llamar desde la propia clase MiJtree
		}
		
	}   

    
//TAREA 4
    private void cargarTabla(String provinSel) {
    	List<Municipio> muniEnprovin = datos.MunicipiosPorProvincia(provinSel);
    	
    	Collections.sort(muniEnprovin, (municipio1, municipio2) -> municipio1.getNombre().compareTo(municipio2.getNombre()));     
    	
    	modeloDatos = new MiTableModel(muniEnprovin);
    	municipioTable.setModel(modeloDatos);
    	
    	municipioTable.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer(){
    		private JProgressBar progressBar = new JProgressBar(50000, 5000000);
    		@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    		
    			if(column == 5) {
    	    	   int poblacion = (Integer) value;
    	    	   
    	    	   if (poblacion < 1000000) {
       	            progressBar.setForeground(Color.green);
       	        	} else if (poblacion < 5000000) {
       	            float normalizedValue = (float) (poblacion - 1000000) / (5000000 - 1000000);
       	            int red = Math.round(normalizedValue * 255);
       	            progressBar.setForeground(new Color(255, 255 - red, 0));
       	        	} else {
       	            progressBar.setForeground(Color.red);
       	        	}
    	    	   
    	    	   progressBar.setValue(poblacion);
    	    	   progressBar.repaint();
    	    	  
    	    	   return progressBar;
    	    	  
    	    	     	    	     	    	   
    			}  else {
    	    	   return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    			}
    			
    		}
    		
    	});
    	
//TAREA 7
    	municipioTable.addMouseListener(new MouseAdapter() {
    		
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int fila = municipioTable.rowAtPoint(e.getPoint());
				int columna = municipioTable.columnAtPoint(e.getPoint());
				int codigoc = (int) municipioTable.getValueAt(fila, 0);
				Municipio seleccionado = null;
				
				if(columna == 1 && fila >= 0 ) {
					//Municipio seleccionado = null;
					for(int i = 0;i < datos.getListaMunicipios().size(); i++) {
						if(datos.getListaMunicipios().get(i).getCodigo() == codigoc) {
							seleccionado = datos.getListaMunicipios().get(i);
						}
					}
					
					PintarCelda(seleccionado,  muniEnprovin);
				}else {
					Restablecer(seleccionado, muniEnprovin);
				}
			}
    		
		});
   
    }
    
    private void Restablecer(Municipio seleccionado, List<Municipio> muniEnProvin) {
    	municipioTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
    		@Override
    		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setBackground(Color.WHITE);
    			return comp;
    		}
    	});
    }
    private void PintarCelda(Municipio seleccionado,  List<Municipio> muniEnprovin) {
    	municipioTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
    		@Override
    		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
    			Municipio Mactual = muniEnprovin.get(row);
    			if(Mactual.getHabitantes()> seleccionado.getHabitantes()) {
    				comp.setBackground(redColor);
    			}else if(Mactual.getHabitantes() == seleccionado.getHabitantes()) {
    				comp.setBackground(Color.WHITE);
    			}else {
    				comp.setBackground(greenColor);
    			}
    			
    	        return comp;
    		}
    		
    	});
    	municipioTable.repaint();
    }

   
//TAREA 4    
    private class MiTableModel implements TableModel {
    	
		private final Class<?>[] CLASES_COLS = { Integer.class, String.class, Integer.class, String.class, String.class, Integer.class };

		//Paso 3
		List<Municipio> listaMunicipios;
    	
    	private MiTableModel(List<Municipio> municipios) {
            this.listaMunicipios = municipios;
        }
    	
    	public void setListaMunicipios(List<Municipio> municipiosEnProvincia) {
    		this.listaMunicipios = municipiosEnProvincia;
    		fireTableChanged(new TableModelEvent(modeloDatos));
    	}
    	
	
		@Override
		public Class<?> getColumnClass(int columnIndex) {	
			return CLASES_COLS[columnIndex]; 
		}

		@Override
		public int getColumnCount() {
			return 6;	// hemos añadido poblacion asiq ahora son 6
		}

		@Override
		public int getRowCount() {			
			return listaMunicipios.size();
		}

		private final String[] cabeceras = { "Código", "Nombre", "Habitantes", "Provincia", "Autonomía", "Poblacion" };
		@Override
		public String getColumnName(int columnIndex) {
			return cabeceras[columnIndex];
		}

		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Municipio municip = listaMunicipios.get(rowIndex);
			
			// System.out.println( "getValueAt " + rowIndex + "," + columnIndex );
			switch (columnIndex) {
			case 0:
				return municip.getCodigo();
			case 1:
				return municip.getNombre();
			case 2:
				return municip.getHabitantes();
			case 3:
				return municip.getProvincia();
			case 4:
				return municip.getAutonomia();
			case 5:
				return municip.getHabitantes();
			default:
				return null;
			}
		
		}

//PASO 6		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			System.out.println( "isCellEditable" );
			if (columnIndex == 1 || columnIndex == 2) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			System.out.println( "setValue " + aValue + "[" + aValue.getClass().getName() + "] " + rowIndex + "," + columnIndex );
			
			Municipio mun = listaMunicipios.get(rowIndex);
			
			switch (columnIndex) {
			case 0:
				mun.setCodigo( (Integer) aValue );
				break;
			case 1:
				mun.setNombre( (String) aValue );
				break;
			case 2:
				try {
				
					mun.setHabitantes( (Integer) aValue );
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog( null, "Nº de habitantes erróneo" );
				}
				break;
			case 3:
				mun.setProvincia( (String) aValue );
				break;
			case 4:
				mun.setAutonomia( (String) aValue );
				break;
			case 5:
				mun.setHabitantes( (Integer) aValue );
				
			}
		}

		ArrayList<TableModelListener> listaEsc = new ArrayList<>();
		@Override
		public void addTableModelListener(TableModelListener l) {
			System.out.println( "addTableModelListener" );
			listaEsc.add( l );
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listaEsc.remove( l );
		}
		
		//Default lo hace asi
		public void fireTableChanged( TableModelEvent e ) { // lanza aviso que se han modificado algunas cosas de la tabla
			for (TableModelListener l : listaEsc) {
				l.tableChanged( e );
			}
		}
		
		public void borraFila( int fila ) {
			if(fila >= 0 && fila < listaMunicipios.size() ) {
				Municipio borrado = listaMunicipios.remove(fila);
				datos.quitar(borrado.getCodigo());
				fireTableChanged( new TableModelEvent( modeloDatos, fila, listaMunicipios.size()));
				municipioTable.repaint();
			}
			
		}
		
	    // Paso 6
	    public void anyadeFila(Municipio muni, int fila) {
	    	listaMunicipios.add(muni);
	//    	fireTableRowsInserted(listaMunicipios.size() - 1, listaMunicipios.size() - 1);
	    	fireTableChanged( new TableModelEvent( modeloDatos, fila, datos.getListaMunicipios().size() ) );  // Para que detecte el cambio en todas
	    }	
	    
	}
    
 
}
