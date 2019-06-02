package objects;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class FileInfoDatabase {

    /*
    * 1 - current position
    * 2 - total
    * 3 - unvisited
    *
    * */
    private ConcurrentHashMap<FileObject,LinkedList<Position>> positions = new ConcurrentHashMap<>();
    private ConcurrentHashMap<FileObject,Integer> cpos;


    class Position {

        public final boolean isVisited;
        public final long val;


        public Position(boolean isVisited, long val) {
            this.isVisited = isVisited;
            this.val = val;
        }


    }

    public void addPositions (FileObject fo, Long val) {
        LinkedList<Position> positions = this.positions.putIfAbsent(fo, new LinkedList<>());
        positions.add(new Position(false,val));

    }

    public Long next (FileObject fo) {
        Integer pos = cpos.getOrDefault(fo,null) ;
        return positions.getOrDefault(fo,null).get(++pos).val;

    }

    public Long previous (FileObject fo ) {
        Integer pos = cpos.getOrDefault(fo,null) ;
        return positions.getOrDefault(fo,null).get(--pos).val;
    }




}
