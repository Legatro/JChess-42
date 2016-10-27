package com.chess.engine.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece.PieceType;
import com.google.common.collect.ImmutableList;

import static com.chess.engine.board.Move.*;

public class Pawn extends Piece{

	private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};
	
	public Pawn(final Alliance pieceAlliance, final int piecePosition) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, true);
	}

	public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
	}

	@Override
	public Collection<Move> calculateLegalMoves(Board board) {
		
		final List<Move> legalMoves = new ArrayList<>();
		
		for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE){
			
			final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection()*currentCandidateOffset);
			
			if(!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
				continue;
			}
			
			if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
				//TODO More work to do here (Deal with promotions)!!!
				legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
			} else if (currentCandidateOffset == 16 && this.isFirstMove() &&
					  ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) || 
					  (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))){
				
				final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection()*8);
				if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
				    !board.getTile(candidateDestinationCoordinate).isTileOccupied()){
					legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
				}
			} else if (currentCandidateOffset == 7 && 
					  !((BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
					  (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						//TODO more to do here
						legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					}
				} else if (board.getEnPassantPawn() != null) {
					if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppasiteDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				}
			} else if (currentCandidateOffset == 9 && 
					  !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceAlliance.isWhite() ||
					  (BoardUtils.EIGHTH_COLUMN[this.piecePosition] && this.pieceAlliance.isBlack())))) {
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
						//TODO more to do here
						legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					} 
				} else if (board.getEnPassantPawn() != null) {
					if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppasiteDirection()))) {
						final Piece pieceOnCandidate = board.getEnPassantPawn();
						if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
							legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}
	
	@Override
	public String toString() {
		return PieceType.PAWN.toString();
	}
	
	@Override
	public Pawn movePiece(final Move move) {
		return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
	}

}