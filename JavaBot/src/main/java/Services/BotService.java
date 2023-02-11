package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject target;
    private boolean targetIsPlayer = false; 
    private GameObject worldCenter;


    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        var heading = 90;
        playerAction.action = PlayerActions.FORWARD;
        // playerAction.heading = new Random().nextInt(360);

        // if (!gameState.getGameObjects().isEmpty()) {
        //     var foodList = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD).sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
            
        //     var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
        //     // if(!nearestPlayer.isEmpty()){
        //     //      System.out.println(nearestPlayer.get(0));
        //     // }
        //     // if(!nearestPlayer.isEmpty()){
        //     //     playerAction.heading = getHeadingBetween(foodList.get(0));
        //     // }
        //     playerAction.heading = 90;
        //     this.playerAction = playerAction;
        // }

        //System.out.println("TEST BOT ACTION : " + playerAction.action + " : " + playerAction.heading);
        if(!gameState.getGameObjects().isEmpty()){
            if(target == null || target == worldCenter)
            {
                System.out.println("NO CURRENT TARGET FOR TEST BOT");
                var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
                if(!nearestPlayer.isEmpty()){
                }
                heading = resolveNewTarget();
            }
            else
            {
                var targetWithNewValues = gameState.getPlayerGameObjects().stream().filter(go -> go.id != target.id).collect(Collectors.toList());
                
                if(targetWithNewValues == null){
                    System.out.println("OLD TARGET INVALID, TEST BOT RESOLVING NEW TARGET");
                    heading = resolveNewTarget();
                }
                else{
                    System.out.println("PREVIOUS TARGET EXIST, TEST BOT UPDATING RESOLUTION");
                    target = targetWithNewValues.get(0);

                    if(target.size < bot.getSize()){
                        heading = getHeadingBetween(target);
                        System.out.println("TARGET IS SMALLER THAN TEST BOT");
                    }
                    else{
                        System.out.println("PREVIOUS TARGET LARGER, TEST BOT RESOLVING NEW TARGET");
                        heading = resolveNewTarget();
                    }
                }
            }
            playerAction.heading = heading;
            GameObject worldC= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
            if(worldC.getPosition() != null){
                var distanceFromWorldCenter = getDistanceBetween(bot, worldC);
                if (distanceFromWorldCenter + (1.5 * bot.getSize()) > gameState.world.getRadius()){
                    worldCenter=new GameObject(null, null, null, null, new Position(0, 0), null);
                    heading = getHeadingBetween(worldCenter);
                    target = worldCenter;
                    System.out.println("BOT AVOIDING VOID, MOVING TO CENTER");
                }
            }

            if (targetIsPlayer)
            {
                System.out.println("Firing Torpedoes at target");
                playerAction.action = PlayerActions.FIRETORPEDOES;
            }
            
            System.out.println("TEST BOT ACTION : " + playerAction.action + " : " + playerAction.heading);
            this.playerAction = playerAction;
        }
    }
    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private int resolveNewTarget()
    {
        int heading;
        var nearestFood = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD).sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
        var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());

        // if(!nearestPlayer.isEmpty()){
        //     var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));
        //     System.out.println(direction2NearestPlayer);
        //     System.out.println("PEMBATAS PLAYER");
        // }
        // if(!nearestFood.isEmpty()){
        //     var direction2NearestFood = getHeadingBetween(nearestFood.get(0));
        //     System.out.println(direction2NearestFood);
        //     System.out.println("PEMBATAS FOOD");
        // }
        // var direction2NearestPlayer = nearestPlayer.isEmpty() ? null : getHeadingBetween(nearestPlayer.get(0));
        // var direction2NearestFood = nearestFood.isEmpty() ? null : getHeadingBetween(nearestFood.get(0));
        if(nearestPlayer.isEmpty()){
            System.out.println("Wa a a a");
        }
        var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));
        
        var direction2NearestFood = getHeadingBetween(nearestFood.get(0));
        
        
        if(nearestPlayer.get(0).getSize() > bot.getSize()){
            System.out.println("TEST BOT KABUR");
            heading = GetAttackerResolution(bot, nearestPlayer.get(0), nearestFood.get(0));
            targetIsPlayer = false;
        }
        else if (nearestPlayer.get(0).getSize() < bot.getSize()){
            heading = direction2NearestPlayer;
            target = nearestPlayer.get(0);
            targetIsPlayer = true;
            System.out.println("TEST BOT CHASING SMALLER PLAYER");
        }
        else if(nearestFood.get(0) != null){
            heading = direction2NearestFood;
            target = nearestFood.get(0);
            targetIsPlayer = false;
            System.out.println("TEST BOT GOING FOR FEEDING");
        }
        else{
            target = worldCenter;
            heading = getHeadingBetween(worldCenter);
            targetIsPlayer = false;
            System.out.println("TEST BOT COULDNT FIND ANYTHING");
        }

        if(target == worldCenter){
            heading = direction2NearestPlayer;
        }

        return heading;
    }


    private int GetAttackerResolution(GameObject bot, GameObject attacker, GameObject closestFood)
    {
        if(closestFood == null){
            return GetOppositeDirection(bot, attacker);
        }
        var distance2Attacker = getDistanceBetween(bot, attacker);
        var distanceBetweenAttackerAndFood = getDistanceBetween(attacker, closestFood);

        if(distance2Attacker > attacker.getSpeed() && distanceBetweenAttackerAndFood > distance2Attacker){
            System.out.println("ATK IS FAR, TEST BOT GOING FOR FOOD");
            return getHeadingBetween(closestFood);
        }
        else{
            System.out.println("TEST BOT RUNNING");
            return GetOppositeDirection(bot, attacker);
        }
    }
    private int GetOppositeDirection(GameObject gameObject1, GameObject gameObject2)
    {
        return toDegrees(Math.atan2(gameObject2.getPosition().y - gameObject1.getPosition().y, gameObject2.getPosition().x - gameObject1.getPosition().x));
    }

}