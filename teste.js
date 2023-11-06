const fs = require('fs');

const numVertices = 1001;
const numEdges = 1000;
const maxWeight = 100;

function generateRandomGraph() {
    const randomGraph = [];

    for (let i = 0; i < numEdges; i++) {
        const srcVertex = Math.floor(Math.random() * numVertices);
        const destVertex = Math.floor(Math.random() * numVertices);
        const weight = Math.floor(Math.random() * maxWeight) + 1;

        randomGraph.push(`${srcVertex} ${destVertex} ${weight}`);
    }

    return randomGraph;
}

function saveGraphToFile(graphData) {
    const graphString = `${numVertices} ${numEdges}\n${graphData.join('\n')}`;

    fs.writeFile('random_graph.txt', graphString, (err) => {
        if (err) {
            console.error(err);
        } else {
            console.log('gerou');
        }
    });
}

const randomGraphData = generateRandomGraph();
saveGraphToFile(randomGraphData);
