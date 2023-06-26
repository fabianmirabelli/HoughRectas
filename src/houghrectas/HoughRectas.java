/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package houghrectas;

/**
 *
 * @author LoreyFaby
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class HoughRectas {
    private static final String imagePath = "Imagen1.jpg";
    private static JFrame frame;
    private static BufferedImage image;
    private static JLabel imageLabel;

    public static void main(String[] args) {
        // Cargar la biblioteca de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Leer la imagen en formato JPG
        Mat imagen = Imgcodecs.imread(imagePath);

        // Convertir la imagen a escala de grises
        Mat grises = new Mat();
        Imgproc.cvtColor(imagen, grises, Imgproc.COLOR_BGR2GRAY);

        // Aplicar suavizado a la imagen en escala de grises
        Mat suavizar = new Mat();
        Imgproc.GaussianBlur(grises, suavizar, new Size(5, 5), 0);

        // Crear el contenedor para mostrar la imagen en un JOptionPane
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Imagen con líneas detectadas");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Mostrar la imagen original en el contenedor
        image = matToBufferedImage(imagen);
        imageLabel = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);

        // Crear un panel para los controles de modificación
        JPanel panel = new JPanel();

        // Crear campos de texto para los parámetros
        JTextField umbralMinField = new JTextField("25", 5);
        JTextField longMinimaLineaField = new JTextField("25", 5);
        JTextField brechaMaxPermitidaField = new JTextField("10", 5);

        // Crear un botón para ejecutar el código con los nuevos parámetros
        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obtener los nuevos valores para los parámetros
                int umbralMin = Integer.parseInt(umbralMinField.getText());
                int longMinimaLinea = Integer.parseInt(longMinimaLineaField.getText());
                int brechaMaxPermitida = Integer.parseInt(brechaMaxPermitidaField.getText());

                // Detectar los bordes en la imagen utilizando el algoritmo Canny
                Mat bordes = new Mat();
                Imgproc.Canny(suavizar, bordes, 30, 90, 3, false);

                // Aplicar la transformada de Hough probabilística para detectar las líneas en los bordes de la imagen
                Mat rectas = new Mat();
                Imgproc.HoughLinesP(bordes, rectas, 1, Math.PI / 180, umbralMin, longMinimaLinea, brechaMaxPermitida);

                // Dibujar las líneas detectadas en la imagen
                Mat imagenConLineas = imagen.clone();
                for (int i = 0; i < rectas.rows(); i++) {
                    double[] line = rectas.get(i, 0);
                    double x1 = line[0];
                    double y1 = line[1];
                    double x2 = line[2];
                    double y2 = line[3];
                    Imgproc.line(imagenConLineas, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0), 1, Imgproc.LINE_AA);
                }

                // Convertir la imagen con líneas a BufferedImage
                image = matToBufferedImage(imagenConLineas);

                // Actualizar la imagen mostrada en el contenedor
                imageLabel.setIcon(new ImageIcon(image));
                frame.pack();
               // Guardar la imagen con líneas detectadas
                Imgcodecs.imwrite("imagen_con_lineas.jpg", imagenConLineas);
                Imgcodecs.imwrite("imagen_contono.jpg", bordes);
            }
        });

        // Agregar los campos de texto y el botón al panel
        panel.add(new JLabel("Umbral:"));
        panel.add(umbralMinField);
        panel.add(new JLabel("LongMinLinea:"));
        panel.add(longMinimaLineaField);
        panel.add(new JLabel("BrechaMaxLinea:"));
        panel.add(brechaMaxPermitidaField);
        panel.add(executeButton);

        // Agregar el panel al contenedor
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        // Mostrar el contenedor
        frame.setVisible(true);
        
       
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Obtener las dimensiones de la imagen
        int ancho = mat.cols();
        int alto = mat.rows();

        // Crear un BufferedImage con el mismo tamaño y tipo de la imagen de OpenCV
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_3BYTE_BGR);

        // Obtener el arreglo de bytes de la imagen de OpenCV
        byte[] dato = new byte[ancho * alto * (int) mat.elemSize()];
        mat.get(0, 0, dato);

        // Establecer los datos de píxeles en la imagen de BufferedImage
        WritableRaster raster = imagen.getRaster();
        raster.setDataElements(0, 0, ancho, alto, dato);

        return imagen;
    }
}
