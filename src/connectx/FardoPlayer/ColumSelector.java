package connectx.FardoPlayer;

import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXGameState;
import connectx.CXPlayer;
import connectx.CXCellState;

public class ColumSelector {
    // private boolean maximizingPlayer;
    private final Integer DIRECTIONS = 7; // down, right, left, up-left, up-right, down-right, down-left
    private final int MOVE_DOWN = -1;
    private final int MOVE_UP = 1;
    private final int MOVE_LEFT = -1;
    private final int MOVE_RIGHT = 1;
    private final int UNDEFINED_MOVE = 0;

    public ColumSelector() {
        // this.maximizingPlayer = maximizingPlayer;
    }
    
    public Integer alphaBeta(CXBoard board, boolean maximizingPlayer, int alpha, int beta){
        if (board.gameState() != CXGameState.OPEN)
            return evaulateBoard(board, maximizingPlayer);
        
        System.out.println(board.toString());
        Integer eval;

        if (maximizingPlayer){
            eval = Integer.MIN_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.max(alphaBeta(board.copy(), false, alpha, beta), eval);
                alpha = Integer.max(alpha, eval);
                if (beta <= alpha){
                    break;
                }
                board.unmarkColumn();
            }
        } else {
            eval = Integer.MAX_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.min(alphaBeta(board.copy(), true, alpha, beta), eval);
                beta = Integer.min(beta, eval);
                if (beta <= alpha){
                    break;
                }
                board.unmarkColumn();
            }
        }

        return eval;
    }


    public Integer alphaBetaDepth(CXBoard board, boolean maximizingPlayer, int alpha, int beta, int depth){
        if (board.gameState() != CXGameState.OPEN || depth == 0)
            return evaulateBoard(board, maximizingPlayer);
        
        System.out.println(board.toString());
        Integer eval;

        if (maximizingPlayer){
            eval = Integer.MIN_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.max(alphaBetaDepth(board.copy(), false, alpha, beta, depth - 1), eval);
                alpha = Integer.max(alpha, eval);
                if (beta <= alpha){
                    break;
                }
                board.unmarkColumn();
            }
        } else {
            eval = Integer.MAX_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.min(alphaBetaDepth(board.copy(), true, alpha, beta, depth - 1), eval);
                beta = Integer.min(beta, eval);
                if (beta <= alpha){
                    break;
                }
                board.unmarkColumn();
            }
        }

        return eval;
    }


    public Integer alphaBetaBFS(CXBoard board){
        
        
        return 0;
    }

    public int evaulateBoard(CXBoard board, boolean maximizingPlayer){
        /*
        *  -5000 per la loss
        *   5000 win
        *   1 pareggio
        *   0 indefinito
        */

        /* int eval = board.gameState() == CXGameState.WINP1 &&  maximizingPlayer ? 5000 : 0;
        eval = board.gameState() == CXGameState.WINP2 && !maximizingPlayer ? 5000 : eval;
        eval = board.gameState() == CXGameState.WINP1 && !maximizingPlayer ? -5000 : eval;
        eval = board.gameState() == CXGameState.WINP2 && maximizingPlayer ? -5000 : eval;
        eval  = board.gameState() == CXGameState.DRAW ? 1 : eval;
        //eval = board.gameState() == CXGameState.OPEN ? 0 : eval;
         */

        Integer eval = 0;

        if (board.gameState() == CXGameState.WINP1 && maximizingPlayer){
            eval = 5000;
        } else if (board.gameState() == CXGameState.WINP2 && !maximizingPlayer){
            eval = 5000;
        } else if (board.gameState() == CXGameState.WINP1 && !maximizingPlayer){
            eval = 5000; // cerco di evitare di perdere
        } else if (board.gameState() == CXGameState.WINP2 && maximizingPlayer){
            eval = 5000;
        } else if (board.gameState() == CXGameState.DRAW){
            eval = 1;
        } else if (board.gameState() == CXGameState.OPEN){
            eval = checkLinearPattherns(board, board.getLastMove()); // - check for the linear pattherns
            System.out.println("patthern: " + eval);
        }

        return eval;
    }

    // check the linear pattherns: -1x = left, 1x = right, -1y = down, 1y = up
    public Integer checkDirection(CXBoard board, CXCell lastMove, int directionX, int directionY){
        Integer shifter = 1;
        CXCellState[][] Mat = board.getBoard();
        CXCellState player = Mat[lastMove.i][lastMove.j];

        if (directionX == MOVE_LEFT && directionY == UNDEFINED_MOVE){
            while (checkIsInMat(board, lastMove.i, lastMove.j - shifter)){
                if (Mat[lastMove.i][lastMove.j - shifter] == player)
                    shifter++;
                else
                    break;
            }
        } else if (directionX == MOVE_RIGHT && directionY == UNDEFINED_MOVE){
            while (checkIsInMat(board, lastMove.i, lastMove.j + shifter)){
                if (Mat[lastMove.i][lastMove.j + shifter] == player)
                    shifter++;
                else
                    break;
            }
        } else if (directionX == MOVE_LEFT && directionY == MOVE_DOWN){
            while (checkIsInMat(board, lastMove.i + shifter, lastMove.j - shifter))
                {
                if (Mat[lastMove.i + shifter][lastMove.j - shifter] == player)
                    shifter++;
                else
                    break;
            }
        } else if (directionX == MOVE_LEFT && directionY == MOVE_UP){
            while (checkIsInMat(board, lastMove.i - shifter, lastMove.j - shifter))
                {
                if (Mat[lastMove.i - shifter][lastMove.j - shifter] == player)
                    shifter++;
                else
                    break;
            }
        }else if (directionX == MOVE_RIGHT && directionY == MOVE_DOWN){
            while (checkIsInMat(board, lastMove.i + shifter, lastMove.j + shifter))
                {
                if (Mat[lastMove.i + shifter][lastMove.j + shifter] == player)
                    shifter++;
                else
                    break;
            }
        } else if (directionX == MOVE_RIGHT && directionY == MOVE_UP){
            while (checkIsInMat(board, lastMove.i - shifter, lastMove.j + shifter))
                {
                if (Mat[lastMove.i - shifter][lastMove.j + shifter] == player)
                    shifter++;
                else
                    break;
            }
        } else if (directionX == UNDEFINED_MOVE && directionY == MOVE_DOWN) { // controllo in basso, in alto non esiste
            while (checkIsInMat(board, lastMove.i + shifter, lastMove.j))
                {
                if (Mat[lastMove.i + shifter][lastMove.j] == player)
                    shifter++;
                else
                    break;
            }
        }

        return shifter;
    }


    protected boolean checkIsInMat(CXBoard board, int row, int col){
        return col >= 0 && col < board.N && row >= 0 && row < board.M;
    }

    public Integer checkLinearPattherns(CXBoard board, CXCell move){
        Integer eval = 0;
        Integer[] directions = new Integer[DIRECTIONS];
        
        // check down direction
        directions[0] = checkDirection(board, move, 0, MOVE_DOWN);
        directions[1] = checkDirection(board, move, MOVE_RIGHT, 0);
        directions[2] = checkDirection(board, move, MOVE_LEFT, 0);
        directions[3] = checkDirection(board, move, MOVE_RIGHT, MOVE_DOWN);
        directions[4] = checkDirection(board, move, MOVE_LEFT, MOVE_DOWN);
        directions[5] = checkDirection(board, move, MOVE_RIGHT, MOVE_UP);
        directions[6] = checkDirection(board, move, MOVE_LEFT, MOVE_UP);

        for (Integer i : directions){
            eval = Integer.max(eval, i);
        }
        
        return eval;
    }

    
}
