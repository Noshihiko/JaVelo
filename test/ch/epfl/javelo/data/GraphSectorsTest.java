package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class GraphSectorsTest {

    @Test
    void GraphSectorWorksNormally() {
        byte[] bufferId = new byte[128 * 128 * 3];

        for (int i = 0; i < bufferId.length; ++i) {
            bufferId[i] = (byte) i;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bufferId);
        PointCh point = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        GraphSectors sectors1 = new GraphSectors(buffer);

        List<GraphSectors.Sector> expected1 = new ArrayList<>();
        expected1.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        assertArrayEquals(expected1.toArray(), sectors1.sectorsInArea(point, 1).toArray());

        List<GraphSectors.Sector> expected2 = new ArrayList<>();
        GraphSectors sectors2 = new GraphSectors(buffer);
        expected2.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(6), buffer.getShort(10) + buffer.getInt(6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(128 * 6), buffer.getShort(128 * 6 + 4) + buffer.getInt(128 * 6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(129 * 6), buffer.getShort(129 * 6 + 4) + buffer.getInt(129 * 6)));
        assertArrayEquals(expected2.toArray(), sectors2.sectorsInArea(point, 2800).toArray());
    }

}

