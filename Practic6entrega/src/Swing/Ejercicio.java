package Swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Ejercicio {
	
	private static JFrame ventana;
	private static DataSetMunicipios dataset;

	private static Ventana ventanaDatos;
	
	public static void main(String[] args) {
		ventana = new JFrame( "Ejercicio" );
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		ventana.setLocationRelativeTo( null );
		ventana.setSize( 200, 80 );

		JButton bCargaMunicipios = new JButton( "Carga municipios > 100k" );
		ventana.add( bCargaMunicipios );
		
		bCargaMunicipios.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cargaMunicipios();
			}
		});
		
		ventana.setVisible( true );
	}
	
	private static void cargaMunicipios() {
		try {
			dataset = new DataSetMunicipios( "munis.txt" );
			System.out.println( "Cargados municipios:" );
			for (Municipio m : dataset.getListaMunicipios() ) {
				System.out.println( "\t" + m );
			}
			// TODO Resolver 
			ventanaDatos = new Ventana( ventana );
			ventanaDatos.setDatos( dataset );
			ventanaDatos.setVisible( true );
			
			
		} catch (IOException e) {
			System.err.println( "Error en carga de municipios" );
		}
	}
	
}