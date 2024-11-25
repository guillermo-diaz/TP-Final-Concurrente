package GUI;

import java.awt.*;

import javax.swing.*;

public class GUI {
    public JFrame frame;
    public JLabel horaLabel;
    public JTextPane[] consolas = new JTextPane[7];
    Thread[] hilos;

    public GUI(Thread[] h){
        hilos = h;
        frame = getFrame();
    }

    public JPanel encabezado(){
         JPanel topPanel = new JPanel(new GridLayout(1, 3)); 

         
         JPanel leftPanel = new JPanel();
         leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
         leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
         leftPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
 
         
         JButton stopButton = new JButton("Detener");
         stopButton.setFont(new Font("Arial", Font.BOLD, 14));
 
         stopButton.setPreferredSize(new Dimension(200, 40));  // Ajusta el tamaño del botón
         stopButton.setBackground(new Color(255, 0, 0));  // Color de fondo rojo
         stopButton.setForeground(Color.WHITE);            // Color de texto blanco
         stopButton.setBorderPainted(false);  
         stopButton.setFocusPainted(false);   
 
         stopButton.addActionListener(e -> {
             for (int i = 0; i < hilos.length; i++) {
                 hilos[i].stop();
             }
         });
 

         leftPanel.add(stopButton);
 
         // Panel para la hora 
         horaLabel = new JLabel("19:20", JLabel.CENTER);
         horaLabel.setFont(new Font("Arial", Font.BOLD, 35));
         horaLabel.setForeground(new Color(50, 50, 50));
         horaLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
 
         topPanel.add(leftPanel);  
         topPanel.add(horaLabel);   
         topPanel.add(new JPanel()); 
         return topPanel;
    }

    public JPanel consolas_actividades(){
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 30, 30));
        gridPanel.setBackground(new Color(243, 243, 243));
        
        String[] titulos = {
            "Restaurante", "Snorkell", "Faro",
            "Tienda", "Acceso Carrera", "Carrera de Gomones"
        };

        for (int i = 0; i < 6; i++) {
            JPanel cellPanel = new JPanel(new BorderLayout());

            JLabel titulo = new JLabel(titulos[i], JLabel.CENTER);
            titulo.setFont(new Font("Arial", Font.BOLD, 16));
            titulo.setForeground(new Color(50, 50, 50));
            titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cellPanel.add(titulo, BorderLayout.NORTH);

            JTextPane textPane = new JTextPane();
            textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textPane.setBackground(new Color(42, 42, 42));
            textPane.setForeground(Color.WHITE);
            textPane.setMargin(new Insets(0, 15, 0, 15));

            consolas[i] = textPane;

            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            cellPanel.add(scrollPane, BorderLayout.CENTER);
            gridPanel.add(cellPanel);
        }
        return gridPanel;
    }

    public JPanel consola_entrada(){
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel tituloInferior = new JLabel("Entrada Parque", JLabel.CENTER);
        tituloInferior.setFont(new Font("Arial", Font.BOLD, 16));
        tituloInferior.setForeground(new Color(50, 50, 50));
        tituloInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottomPanel.add(tituloInferior, BorderLayout.NORTH);

        JTextPane consolaInferior = new JTextPane();
        consolaInferior.setFont(new Font("Monospaced", Font.PLAIN, 14));
        consolaInferior.setBackground(new Color(42, 42, 42));
        consolaInferior.setForeground(Color.WHITE);
        consolaInferior.setMargin(new Insets(0, 15, 0, 15));

        consolas[6] = consolaInferior;

        JScrollPane scrollPaneInferior = new JScrollPane(consolaInferior);
        scrollPaneInferior.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneInferior.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bottomPanel.add(scrollPaneInferior, BorderLayout.CENTER);
        bottomPanel.setPreferredSize(new Dimension(0, 120)); 

        return bottomPanel;
    }

    public JFrame getFrame(){
        JFrame frame = new JFrame("ECO-Park");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel topPanel = encabezado();
        JPanel gridPanel = consolas_actividades();
        JPanel bottomPanel = consola_entrada();

        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(mainPanel);

        return frame;
}
}

