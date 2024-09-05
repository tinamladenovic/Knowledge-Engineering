package upgrade;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.ReasonerFactory;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class MemoryUpgradeRecommendation {

    public static void main(String[] args) {
        try {
            // Učitavanje ontologije
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File ontologyFile = new File("/WEB-INF/resources/ontologyIZ.owx");  
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

            // Inicijalizacija rezonera (Hermit)
            OWLReasonerFactory reasonerFactory = new ReasonerFactory();
            OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

            // Prekompjutovanje inferencija za klasu hijerarhije
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

            // Pronalaženje svih matičnih ploča (klasa MotherBoard)
            OWLDataFactory dataFactory = manager.getOWLDataFactory();
            OWLClass motherboardClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#MotherBoard"));
            Set<OWLNamedIndividual> motherboardIndividuals = reasoner.getInstances(motherboardClass, false).getFlattened();

            // Provera da li su pronađene matične ploče
            if (motherboardIndividuals.isEmpty()) {
                System.out.println("No motherboards found.");
            } else {
                System.out.println("Found motherboards:");
            }

            for (OWLNamedIndividual motherboard : motherboardIndividuals) {
                System.out.println("Motherboard: " + motherboard.getIRI().getFragment());

                // Pozivanje metode za dobijanje kompatibilnih memorijskih modula
                Set<OWLNamedIndividual> compatibleModules = getCompatibleMemoryModules(reasoner, motherboard, dataFactory);

                System.out.println("Compatible memory modules for " + motherboard.getIRI().getFragment() + ":");
                if (compatibleModules.isEmpty()) {
                    System.out.println("No compatible memory modules found.");
                }

                for (OWLNamedIndividual module : compatibleModules) {
                    System.out.println("Memory Module: " + module.getIRI().getFragment());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metod za dobijanje kompatibilnih memorijskih modula
    private static Set<OWLNamedIndividual> getCompatibleMemoryModules(OWLReasoner reasoner, OWLNamedIndividual motherboard, OWLDataFactory dataFactory) {
        Set<OWLNamedIndividual> compatibleModules = new HashSet<>();

        try {
            // Pronalaženje hasRAMType svojstva matične ploče
            OWLObjectProperty hasRAMType = dataFactory.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#hasRAMType"));
            NodeSet<OWLNamedIndividual> ramTypeNodes = reasoner.getObjectPropertyValues(motherboard, hasRAMType);

            if (!ramTypeNodes.isEmpty()) {
                // Pronalazimo RAM tip (DIMM-DDR3, DIMM-DDR4 itd.)
                OWLNamedIndividual ramType = ramTypeNodes.getFlattened().iterator().next();
                String ramTypeName = ramType.getIRI().getFragment();
                System.out.println("RAM Type: " + ramTypeName);

                // Pronađi sve module koji odgovaraju ovom RAM tipu
                OWLClass memoryModulClass = dataFactory.getOWLClass(IRI.create("http://www.semanticweb.org/nina/ontologies/2024/6/untitled-ontology-8#MemoryModul"));
                NodeSet<OWLNamedIndividual> allMemoryModules = reasoner.getInstances(memoryModulClass, false);

                for (OWLNamedIndividual module : allMemoryModules.getFlattened()) {
                    String memoryName = module.getIRI().getFragment();

                    // Ako memorijski modul odgovara RAM tipu (npr. DDR4) i ima veći kapacitet od unetog
                    if (memoryName.contains(ramTypeName)) {
                        compatibleModules.add(module);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return compatibleModules;
    }
}
