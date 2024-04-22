const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const AWS = require('aws-sdk');

// Configure AWS SDK
AWS.config.update({
    region: '',
    accessKeyId: '',
    secretAccessKey: '',
    sessionToken: ''
});

// Create Athena instance
const athena = new AWS.Athena();

// Middleware to parse JSON bodies
app.use(bodyParser.json());

// Define API route
app.post('/query', async (req, res) => {
    console.log(req.body)
    const query = req.body.query;

    // Construct the query parameters
    const params = {
        QueryString: query,
        ResultConfiguration: {
            OutputLocation: 's3://csci5409-bucket/query_results/'
        }
    };

    try {
        // Execute the query
        const data = await athena.startQueryExecution(params).promise();
        const queryExecutionId = data.QueryExecutionId;

        // Wait for the query to complete
        await waitForQueryCompletion(queryExecutionId);

        // Get query results
        const results = await getQueryResults(queryExecutionId);

        res.json(results);
    } catch (error) {
        console.log("Error: " + error)
        res.status(500).json({ error: error.message });
    }
});

// Function to wait for query completion
async function waitForQueryCompletion(queryExecutionId) {
    const params = {
        QueryExecutionId: queryExecutionId
    };

    let status = '';

    do {
        const data = await athena.getQueryExecution(params).promise();
        status = data.QueryExecution.Status.State;
        
        if (status === 'FAILED' || status === 'CANCELLED') {
            throw new Error('Query execution failed or was cancelled');
        }

        await new Promise(resolve => setTimeout(resolve, 1000)); // Wait for 1 second before checking again
    } while (status !== 'SUCCEEDED');
}

// Function to get query results
async function getQueryResults(queryExecutionId) {
    const params = {
        QueryExecutionId: queryExecutionId
    };

    const data = await athena.getQueryResults(params).promise();
    return formatData(data);
}

function formatData(data) {
    // Extract column names from the first row
    const columnNames = data.ResultSet.Rows[0].Data.map(col => col.VarCharValue);

    const rows = data.ResultSet.Rows.slice(1); // Exclude the first two rows which contain column names and empty values

    const transformedData = rows.map(row => {
        const rowData = {};
        row.Data.forEach((col, index) => {
            rowData[columnNames[index]] = col.VarCharValue;
        });
        return rowData;
    });

    const output = {
        data: transformedData
    };

    return output;
}

// Start the server
const PORT = process.env.PORT || 80;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
