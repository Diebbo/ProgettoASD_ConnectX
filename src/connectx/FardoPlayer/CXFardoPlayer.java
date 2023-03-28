package connectx.FardoPlayer;

import connectx.CXPlayer;
import connectx.CXBoard;

public class CXFardoPlayer implements CXPlayer{
    public int M;
    public int N;
    public int X;
    public boolean first;
    public int timeout_in_secs;

    public CXFardoPlayer() {
    }

    public String playerName() {
        return "FardoPlayer";
    }

    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.X = X;
        this.first = first;
        this.timeout_in_secs = timeout_in_secs;
    }

    public int selectColumn(CXBoard B) {
        ColumSelector selector = new ColumSelector();
        Integer selectedColumn = 0;
        Integer evalColumns = Integer.MIN_VALUE;
        
        //selector.alphaBeta(B, -1, 1);
        for (Integer i : B.getAvailableColumns()){
            B.markColumn(i);
            Integer currentColumn = selector.alphaBeta(B, first, -1, 1);
            
            if (currentColumn > evalColumns){
                evalColumns = currentColumn;
                selectedColumn = i;
                break;
            }
            B.unmarkColumn();
        }



        return selectedColumn;
    }
    
}
