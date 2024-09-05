<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Computer Specification Input</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        /* Osnovni stil za omogućavanje skrolovanja */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            min-height: 100vh; /* Omogućava skrolovanje */
            display: block; /* Prirodno ponašanje stranice */
        }

        /* Kontejner za formu */
        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 800px;
            margin: 20px auto; /* Centriranje i omogućavanje skrolovanja */
            width: 100%;
        }

        h1 {
            text-align: center;
            color: #333;
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: bold;
            color: #555;
        }

        input {
            width: 100%;
            padding: 8px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 16px;
        }

        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            font-size: 18px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }

        /* Stil za grafikon */
        #resultsChart {
            max-width: 100%;
            width: 800px;
            height: 500px; /* Povećana visina */
            margin: 20px auto;
        }

        /* Link back to index */
        a {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #007BFF;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Enter Computer Specifications for Evaluation</h1>
        <form action="EvaluateServlet" method="post">
            
            <!-- Унос за брзину процесора -->
            <label for="cpuSpeed">CPU Speed (GHz):</label>
            <input type="text" id="cpuSpeed" name="cpuSpeed" value="<%= request.getParameter("cpuSpeed") != null ? request.getParameter("cpuSpeed") : "" %>">

            <!-- Унос за број тредова -->
            <label for="threadNumber">Thread Number:</label>
            <input type="text" id="threadNumber" name="threadNumber" value="<%= request.getParameter("threadNumber") != null ? request.getParameter("threadNumber") : "" %>">

            <!-- Унос за број језгара -->
            <label for="cpuCores">CPU Cores:</label>
            <input type="text" id="cpuCores" name="cpuCores" value="<%= request.getParameter("cpuCores") != null ? request.getParameter("cpuCores") : "" %>">

            <!-- Унос за величину складишта -->
            <label for="storageSize">Storage Size (GB):</label>
            <input type="text" id="storageSize" name="storageSize" value="<%= request.getParameter("storageSize") != null ? request.getParameter("storageSize") : "" %>">

            <!-- Унос за величину RAM-а -->
            <label for="memorySize">RAM Size (GB):</label>
            <input type="text" id="memorySize" name="memorySize" value="<%= request.getParameter("memorySize") != null ? request.getParameter("memorySize") : "" %>">

            <!-- Унос за величину GPU меморије -->
            <label for="gpuFrequency">GPU Dedicated Memory (Hz):</label>
            <input type="text" id="gpuFrequency" name="gpuFrequency" value="<%= request.getParameter("gpuFrequency") != null ? request.getParameter("gpuFrequency") : "" %>">

            <!-- Дугме за слање -->
            <input type="submit" value="Evaluate">
        </form>

        <!-- Grafikon za prikaz rezultata ako je dostupan -->
        <canvas id="resultsChart"></canvas>
        
        <%
            if (request.getAttribute("results") != null) {
                Map<String, Double> results = (Map<String, Double>) request.getAttribute("results");
        %>
        <script>
            const ctx = document.getElementById('resultsChart').getContext('2d');
            const resultsChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ['Home Use', 'Business Use', 'Gaming', 'Mining', 'Hosting'],
                    datasets: [{
                        label: 'Suitability',
                        data: [
                            <%= results.get("homeUseSuitability") %>,
                            <%= results.get("businessUseSuitability") %>,
                            <%= results.get("gamingSuitability") %>,
                            <%= results.get("miningSuitability") %>,
                            <%= results.get("hostingSuitability") %>
                        ],
                        backgroundColor: [
                            'rgba(75, 192, 192, 0.2)',
                            'rgba(54, 162, 235, 0.2)',
                            'rgba(255, 206, 86, 0.2)',
                            'rgba(153, 102, 255, 0.2)',
                            'rgba(255, 159, 64, 0.2)'
                        ],
                        borderColor: [
                            'rgba(75, 192, 192, 1)',
                            'rgba(54, 162, 235, 1)',
                            'rgba(255, 206, 86, 1)',
                            'rgba(153, 102, 255, 1)',
                            'rgba(255, 159, 64, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: 1
                        }
                    }
                }
            });
        </script>
        <%
            }
        %>
        <a href="index.jsp">Back to Index</a>
    </div>
</body>
</html>
