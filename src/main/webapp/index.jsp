<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Index</title>
    <style>
        /* Celi sadržaj je centriran i stilizovan */
        body {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f4f4f9;
            font-family: Arial, sans-serif;
        }

        h1 {
            text-align: center;
            color: #333;
        }

        /* Kontejner za dugmad */
        .button-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: center;
            gap: 20px; /* Razmak između dugmadi */
            max-width: 600px;
            width: 100%;
        }

        /* Stilizacija dugmadi */
        input[type="submit"] {
            padding: 10px 20px;
            font-size: 18px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <h1>Computer Store and Service Support System</h1>


    <!-- Kontejner koji centira dugmad jedno pored drugog -->
    <div class="button-container">
        <form action="evaluatePage.jsp" method="get">
            <input type="submit" value="Evaluate Computer">
        </form>

        <form action="findSimilarPage.jsp" method="get">
            <input type="submit" value="Find Similar Computers">
        </form>

        <!-- Dodato dugme za Upgrade -->
        <form action="upgradePage.jsp" method="get">
            <input type="submit" value="Upgrade Computer">
        </form>
    </div>
</body>
</html>
