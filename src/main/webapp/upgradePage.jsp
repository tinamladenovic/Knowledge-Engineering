<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Memory Upgrade Tool</title>
    <style>
        /* Stil za omogućavanje skrolovanja */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            min-height: 100vh;
            display: block;
        }

        .form-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 800px;
            margin: 20px auto;
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

        select, input[type="text"], input[type="submit"] {
            padding: 8px;
            margin-top: 10px;
            margin-bottom: 20px;
            width: 100%;
            box-sizing: border-box;
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

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        table, th, td {
            border: 1px solid #ddd;
        }

        th, td {
            padding: 8px;
            text-align: left;
        }

        th {
            background-color: #4CAF50;
            color: white;
        }

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
        <h1>Memory Upgrade Tool</h1>

        <!-- Prikaz greške, ako postoji -->
        <c:if test="${not empty error}">
            <p style="color: red; text-align: center;">${error}</p>
        </c:if>

        <form action="UpgradeServlet" method="post">
            <!-- Izbor matične ploče -->
            <label for="motherboard">Select Motherboard:</label>
            <select name="motherboard" id="motherboard" required>
                <option value="" disabled selected>Select a motherboard</option>
                <option value="ASRock_X570_Phantom_Gaming4">ASRock X570 Phantom Gaming4</option>
                <option value="ASUS_Prime_H610M-K_D4">ASUS Prime H610M-K D4</option>
                <option value="ASUS_ROG_Strix_Z690-E_Gaming">ASUS ROG Strix Z690-E Gaming</option>
                <option value="Gigabyte_B550_AORUS_ELITE_V2">Gigabyte B550 AORUS ELITE V2</option>
                <option value="Gigabyte_B660M_DS3H_DDR4">Gigabyte B660M DS3H DDR4</option>
                <option value="MSI_MPG_B550_Gaming_Edge">MSI MPG B550 Gaming Edge</option>
            </select>

            <!-- Unos trenutnog kapaciteta memorije -->
            <label for="currentMemory">Enter Current Memory (in GB):</label>
            <input type="text" name="currentMemoryCapacity" id="currentMemory" placeholder="e.g. 8, 16, 32" required />

            <!-- Dugme za pokretanje nadogradnje -->
            <input type="submit" value="Find Upgrade">
        </form>

        <c:choose>
            <c:when test="${not empty upgradeModules}">
                <h2>Available Memory Upgrades:</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Memory Module</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="module" items="${upgradeModules}">
                            <tr>
                                <td>${module}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
              
            </c:otherwise>
        </c:choose>

       
         <a href="index.jsp">Back to Index</a>
    </div>

</body>
</html>
