package fuzzysystem;

import net.sourceforge.jFuzzyLogic.FIS;

public class FuzzySystemDAO {

    // Ова метода сада прима апсолутни пут до .fcl датотеке
    public double evaluateComputer(String fileName, int cpuCores, double cpuSpeed, int threadNumber, int storageSize, int memorySize, int gpuDedicatedMemory, String usageType) {
        
        // Учитавање .fcl датотеке
        FIS fis = FIS.load(fileName, true);

        if (fis == null) {
            System.err.println("Cannot load file: " + fileName);
            return 0;
        }

        // Постављање улазних вредности у фази систем
        fis.setVariable("cores", cpuCores);                   
        fis.setVariable("cpuSpeed", cpuSpeed);                
        fis.setVariable("threadNumber", threadNumber);        
        fis.setVariable("storageSize", storageSize);          
        fis.setVariable("ram", memorySize);                  
        fis.setVariable("gpuDedicatedMemory", gpuDedicatedMemory); 

        // Одређивање одговарајуће излазне променљиве на основу типa употребе
        String suitabilityVar;
        switch (usageType.toLowerCase()) {
            case "home":
                suitabilityVar = "homeUseSuitability";
                break;
            case "business":
                suitabilityVar = "businessUseSuitability";
                break;
            case "gaming":
                suitabilityVar = "gamingSuitability";
                break;
            case "mining":
                suitabilityVar = "miningSuitability";
                break;
            case "hosting":
                suitabilityVar = "hostingSuitability";
                break;
            default:
                System.err.println("Unknown usage type: " + usageType);
                return 0;
        }

        // Извршење фази система
        fis.evaluate();

        // Преузимање резултата за одређену намену
        return fis.getVariable(suitabilityVar).getValue();
    }
}
