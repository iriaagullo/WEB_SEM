package entregable.jena;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       // Crear modelo RDF vacío
        Model model = ModelFactory.createDefaultModel();

        // Definir prefijos
        String baseURI = "http://example.org/transport#";
        String geoURI = "http://www.w3.org/2003/01/geo/wgs84_pos#";
        String xsdURI = "http://www.w3.org/2001/XMLSchema#";

        model.setNsPrefix("ex", baseURI);
        model.setNsPrefix("geo", geoURI);
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("xsd", xsdURI);

        // Definir propiedades
        Property stopName = model.createProperty(baseURI, "stopName");
        Property lat = model.createProperty(geoURI, "lat");
        Property lon = model.createProperty(geoURI, "long");

        try (BufferedReader br = Files.newBufferedReader(Paths.get("src/resources/stops.txt"))) {
            String line = br.readLine(); // Leer cabecera y saltarla
            if (line != null && line.contains("stop_id")) line = br.readLine();

            while (line != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 12) {
                    String id = parts[0].trim();
                    String name = parts[2].trim();
                    String latitude = parts[4].trim();
                    String longitude = parts[5].trim();

                    if (!id.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()) {
                    // Crear recurso para la parada
                    Resource stop = model.createResource(baseURI + id)
                            .addProperty(RDF.type, model.createResource(baseURI + "Stop"))
                            .addProperty(RDF.type, model.createResource(geoURI + "SpatialThing"))
                            .addProperty(RDFS.label, model.createLiteral(name, "es"))
                            .addProperty(lat, model.createTypedLiteral(latitude, XSDDatatype.XSDdecimal))
                            .addProperty(lon, model.createTypedLiteral(longitude, XSDDatatype.XSDdecimal));
                    }
                }
                line = br.readLine();
            }

            // Guardar grafo RDF en formato Turtle
            model.write(new FileOutputStream("src/resources/stops.ttl"), "TURTLE");
            System.out.println("✅ Grafo RDF generado correctamente: src/resources/stops.ttl");

        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
