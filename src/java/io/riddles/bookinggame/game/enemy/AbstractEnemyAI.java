/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.bookinggame.game.enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.stream.Collectors;

import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.move.MoveType;

/**
 * io.riddles.bookinggame.game.enemy.AbstractEnemy - Created on 29-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
abstract class AbstractEnemyAI implements EnemyAIInterface {

    Point getMovedCoordinate(Point coordinate, MoveType moveType) {
        if (moveType != null) {
            switch(moveType) {
                case UP:
                    return new Point(coordinate.x, coordinate.y - 1);
                case DOWN:
                    return new Point(coordinate.x, coordinate.y + 1);
                case RIGHT:
                    return new Point(coordinate.x + 1, coordinate.y);
                case LEFT:
                    return new Point(coordinate.x - 1, coordinate.y);
            }
        }
        return coordinate;
    }

    boolean isEmptyInDirection(Point coordinate, MoveType moveType, BookingGameBoard board) {
        if (moveType == null) {
            return false;
        }

        Point newCoordinate = getMovedCoordinate(coordinate, moveType);
        return board.isCoordinateValid(newCoordinate);
    }

    MoveType getDirection(Point oldCoordinate, Point newCoordinate) {
        if (newCoordinate.x > oldCoordinate.x) return MoveType.RIGHT;
        if (newCoordinate.x < oldCoordinate.x) return MoveType.LEFT;
        if (newCoordinate.y > oldCoordinate.y) return MoveType.DOWN;
        if (newCoordinate.y < oldCoordinate.y) return MoveType.UP;
        return null;
    }

    ArrayList<MoveType> getAvailableDirections(Enemy enemy, BookingGameBoard board) {
        return MoveType.getMovingMoveTypes().stream()
                .filter(moveType ->
                        !moveType.equals(enemy.getDirection().getOppositeMoveType()) &&
                            isEmptyInDirection(enemy.getCoordinate(), moveType, board))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    Point mandatoryTranform(Enemy enemy) {
        ArrayList<MoveType> movingMoveTypes = (ArrayList<MoveType>) MoveType.getMovingMoveTypes();

        switch (movingMoveTypes.size()) {
            // No directions available (stuck), stay in place
            case 0:
                return enemy.getCoordinate();
            // Only one direction available, go that way
            case 1:
                return getMovedCoordinate(enemy.getCoordinate(), movingMoveTypes.get(0));
            // Not on a crossroad, continue same direction
            case 2:
                return getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
        }

        return null;
    }
}