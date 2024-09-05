<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Similar Computers</title>
    <style>
        /* Celi sadržaj je centriran i stilizovan */
        body {
            margin: 0;
            padding: 0;
            background-color: #f4f4f9;
            font-family: Arial, sans-serif;
        }

        /* Fleksibilna visina sa mogućnošću skrolovanja */
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            min-height: 100vh; /* Omogućava skrolovanje */
        }

        h1 {
            text-align: center;
            color: #333;
        }

        /* Kontejner za formu */
        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 100%;
            margin-bottom: 20px; /* Prostor između forme i rezultata */
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: bold;
            color: #555;
        }

        select {
            width: 100%;
            padding: 8px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 16px;
        }

        button {
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            font-size: 18px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        /* Stil za rezultate */
        #results {
            margin-top: 20px;
            max-width: 600px;
            width: 100%;
        }

        /* Stil za svaki pojedinačni računar */
        .computer-item {
            padding: 10px;
            background-color: #f0f0f0;
            margin-bottom: 10px;
            border-radius: 5px;
            font-weight: bold;
            color: #333;
        }

    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h1>Search for Similar Computers</h1>
            <form id="findSimilarForm">
                <label for="cpuCores">CPU Model:</label>
                <select id="cpuCores" name="cpuCores" required>
                    <option value="">Select CPU Model</option> <!-- Prazna opcija na početku -->
                    <option value="Intel_Corei5_10400F_Hexa_Core_Processor">Intel Core i5 10400F</option>
                    <option value="Intel_Corei9_12900K_Processor">Intel Core i9 12900K</option>
                    <option value="Intel_Corei7_12700K_Processor">Intel Core i7 12700K</option>
                    <option value="AMD_Ryzen7_5800X_CPU">AMD Ryzen 7 5800X</option>
                    <option value="Intel_Corei5_13600K_Processor">Intel Core i5 13600K</option>
                    <option value="AMD_Ryzen5_5500_CPU">AMD Ryzen 5 5500</option>
                    <option value="Intel_Corei7_12700KF_Processor">Intel Core i7 12700KF</option>
                    <option value="AMD_Ryzen9_5900X_CPU">AMD Ryzen 9 5900X</option>
                    <option value="Intel_Corei9_13900K_Processor">Intel Core i9 13900K</option>
                </select>

                <label for="memorySize">Memory Type (RAM):</label>
                <select id="memorySize" name="memorySize" required>
                    <option value="">Select Memory Type</option> <!-- Prazna opcija na početku -->
                    <option value="DIMM-DDR4">DDR4</option>
                    <option value="DIMM-DDR5">DDR5</option>
                    <option value="DIMM-DDR4-ECC">DDR4-ECC</option>
                    <option value="DIMM-DDR4-RAM_Slot">DDR4 RAM Slot</option>
                    <option value="DIMM-DDR3L">DDR3L</option>
                    <option value="SO-DIMM-DDR4">SO-DIMM DDR4</option>
                </select>

                <label for="gpuModel">GPU Model:</label>
                <select id="gpuModel" name="gpuModel" required>
                    <option value="">Select GPU Model</option> <!-- Prazna opcija na početku -->
                    <option value="Gigabyte_GF_RTX3060">Gigabyte GF RTX 3060</option>
                    <option value="GeForce_RTX_3080">GeForce RTX 3080</option>
                    <option value="GeForce_RTX_3050">GeForce RTX 3050</option>
                    <option value="GeForce_RTX_3600Ti">GeForce RTX 3600Ti</option>
                    <option value="GeForce_RTX_4070">GeForce RTX 4070</option>
                </select>

                <button type="submit">Search</button>
            </form>
        </div>

        <div id="results"></div>
         <a href="index.jsp">Back to Index</a>
    </div>

    <script>
    document.getElementById('findSimilarForm').addEventListener('submit', function(event) {
        event.preventDefault();

        const cpuCores = document.getElementById('cpuCores').value;
        const memorySize = document.getElementById('memorySize').value;
        const gpuModel = document.getElementById('gpuModel').value;

        const formData = new URLSearchParams();
        if (cpuCores) formData.append('cpuCores', cpuCores);
        if (memorySize) formData.append('memorySize', memorySize);
        if (gpuModel) formData.append('gpuModel', gpuModel);

        // Pošaljite POST zahtev na pravi URL
        fetch('/KnowledgeEngineering/SimilarityServlet', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Server error: ' + response.statusText);
            }
            return response.text();
        })
        .then(data => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';  // Očisti prethodne rezultate

            // Prikaz rezultata kao lepog teksta
            const parser = new DOMParser();
            const doc = parser.parseFromString(data, 'text/html');
            const computers = doc.querySelectorAll('.computer-item');
            if (computers.length === 0) {
                resultsDiv.innerHTML = '<p>No similar computers found.</p>';
            } else {
                computers.forEach(computer => {
                    const div = document.createElement('div');
                    div.className = 'computer-item';
                    div.innerHTML = computer.innerHTML;
                    resultsDiv.appendChild(div);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while searching for similar computers.');
        });
    });
    
   
    </script>
   
</body>
</html>
