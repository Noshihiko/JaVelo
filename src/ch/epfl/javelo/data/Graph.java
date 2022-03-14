package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;

public final class Graph {

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {

    }

    public static Graph loadFrom(Path basePath) throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        Path attributesPath = basePath.resolve("attributes.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path elevationsPath = basePath.resolve("elevations.bin");
        Path nodesPath = basePath.resolve("nodes.bin");
        Path profile_idsPath = basePath.resolve("profile_ids.bin");
        Path sectorsPath = basePath.resolve("sectors.bin");

        ShortBuffer 

        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
}
