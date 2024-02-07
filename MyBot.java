package bots;

import com.gats.manager.*;
import com.badlogic.gdx.math.Vector2;
import com.gats.simulation.*;

public class MyBot extends Bot {
    int turn = 0;
    boolean healing = false;

    /**
     * Hier gebt ihr euren Namen an
     *
     * @return Format: Vorname Nachname
     */
    @Override
    public String getStudentName() {
        return "Abdalla Ahmed";
    }

    /**
     * Hier gebt ihr eure Matrikelnummer an
     *
     * @return eure Matrikelnummer
     */
    @Override
    public int getMatrikel() {
        return 236953; // ToDo
    }

    /**
     * Hier könnt ihr eueren Bot einen (kreativen) Namen geben
     *
     * @return Name des Bots
     */
    @Override
    public String getName() {
        return "Abdo_Bot";
    }

    /**
     * Diese Methode wird beim Laden der Map aufgerufen. Ideal um gegebenfalls Werte zu initialisieren
     *
     * @param state Der {@link GameState Spielzustand} zu Beginn des Spiels
     */
    @Override
    protected void init(GameState state) {
        // ToDo
        turn = 0;
    }

    /**
     * Diese Methode beschreibt den Zug den eure Gadse macht
     *
     * @param state      Der {@link GameState Spielzustand} während des Zuges -> Spielinformationen
     * @param controller Der {@link Controller Controller}, zum Charakter gehört, welcher am Zug ist -> Charaktersteuerung
     */
    @Override
    protected void executeTurn(GameState state, Controller controller) {
        Vector2 position = controller.getGameCharacter().getPlayerPos();
        healing = false;

        WeaponType weapon;
        Vector2 angle = new Vector2();
        double d = Double.MAX_VALUE;
        if (controller.getGameCharacter().getHealth() <= 40) {
            int xTile = 0;
            int yTile = 0;
            int x = state.getBoardSizeX();
            int y = state.getBoardSizeY();
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    if (state.getTile(i, j) != null && state.getTile(i, j).getTileType().equals(Tile.TileType.HEALTH_BOX)) {
                        Vector2 healthTile = state.getTile(i, j).getWorldPosition();
                        double curD = Math.sqrt(Math.pow(Math.abs(healthTile.x - position.x), 2) + Math.pow(Math.abs(healthTile.y - position.y), 2));
                        if (curD < d) {
                            xTile = i;
                            yTile = j;
                            d = curD;
                        }
                    }
                }
            }
            Vector2 healthTile = state.getTile(xTile, yTile).getWorldPosition();
            angle.x = healthTile.x - position.x;
            angle.y = healthTile.y - position.y;

            int s = 1;
            if (d < 160) {
                healing = true;
                while (angle.x >= 16 && state.getTile((int) (position.x / 16) + s, (int) (position.y / 16) - 1) != null && state.getTile((int) (position.x / 16) + s, (int) (position.y / 16)) == null && controller.getGameCharacter().getStamina() >= 16) {
                    controller.move(16);
                    angle.x -= 16;
                    s++;
                }
                while (angle.x <= 16 && state.getTile((int) (position.x / 16) - s, (int) (position.y / 16) - 1) != null && state.getTile((int) (position.x / 16) - s, (int) (position.y / 16)) == null && controller.getGameCharacter().getStamina() >= 16) {
                    controller.move(-16);
                    angle.x += 16;
                    s++;
                }
            } else d = Double.MAX_VALUE;

        }

        int s = 0;
        GameCharacter[] closest3 = new GameCharacter[3];
        for (int i = 0; i < 4; i++) {
            if (i == controller.getGameCharacter().getTeam()) continue;

            for (int j = 0; j < 3; j++) {

                if (state.getCharacterFromTeams(i, j).isAlive()) {
                    GameCharacter cur = state.getCharacterFromTeams(i, j);
                    if (closest3[0] == null){
                        closest3[0] = cur;
                    }else {
                        if (closest3[0].getPlayerPos().dst(position) > cur.getPlayerPos().dst(position)){
                            closest3[0] = cur;
                        }
                    }
                }
                sortGC(closest3,position);
            }
        }

        int[] health = new int[3];
        if (closest3[2] != null){
            if (closest3[2].getPlayerPos().dst(position) >= 160 ){
                health[2] = 101;
            }else health[2] = closest3[2].getHealth();
        }
        if (closest3[1] != null){
            if (closest3[1].getPlayerPos().dst(position) >= 160 ) {
                health[1] = 101;
            }else health[1] = closest3[1].getHealth();
        }
        if (closest3[0] != null){
            health[0] = closest3[0].getHealth();
        }else health[0] = 101;

        Vector2 opPosition;
        int worth;
        try {
            if (closest3[2] != null && health[2] <= health[1] && health[2] <= health[0]){
                opPosition = closest3[2].getPlayerPos();
                worth  = 2;
            } else if (closest3[1] != null && health[1] <= health[0] ) {
                opPosition = closest3[1].getPlayerPos();
                worth = 1;
            } else {
                opPosition = closest3[0].getPlayerPos();
                worth = 0;
            }
        }catch (Exception e){
            opPosition = new Vector2(1,0);
            worth =  -1;
        }
        if (!healing || (worth>=0 && closest3[worth].getHealth() <= 10)){

            angle.x = opPosition.x - position.x;
            angle.y = opPosition.y - position.y;

            while (angle.x >= 96 && state.getTile((int) (position.x / 16) + 1 + s, (int) (position.y / 16) - 1) != null && state.getTile((int) (position.x / 16) + 1 + s, (int) (position.y / 16)) == null && controller.getGameCharacter().getStamina() >= 16) {
                controller.move(16);
                angle.x -= 16;
                s++;
            }
            while (angle.x <= -96 && state.getTile((int) (position.x / 16) - 1 + s, (int) (position.y / 16) - 1) != null && state.getTile((int) (position.x / 16) - 1 + s, (int) (position.y / 16)) == null && controller.getGameCharacter().getStamina() >= 16) {
                controller.move(-16);
                angle.x += 16;
                s--;
            }
        }



//        if (angle.y < 0 && state.getTile((int) (position.x / 16) + s, (int) (position.y / 16) - 2) == null){
//            while (angle.angle() >= 230 && angle.angle() <= 310){
//                angle.y++;
//            }
//        }

        if ( turn >= 6  && controller.getGameCharacter().getWeapon(2).getAmmo() > 0 && closest3[worth].getHealth()>=35) {
            weapon = WeaponType.MIOJLNIR;
        } else weapon = WeaponType.WATER_PISTOL;

        if (weapon == WeaponType.WATER_PISTOL && Math.abs(angle.x) >= 80){
            angle.y += Math.abs(angle.x)*0.1225;
        }
        controller.shoot(angle, 1.0f, weapon);
        turn++;
    }

    private void sortGC(GameCharacter[] arr ,Vector2 myPos){
        if (arr[0] == null){
            return;
        }
        if (arr[1] == null){
            arr[1] = arr[0];
            arr[0] = null;
            return;
        }
        if (arr[2] == null){
            arr[2] = arr[0];
            arr[0] = null;
            return;
        }

        swap(0,1, myPos, arr);
        swap(1,2, myPos, arr);
        swap(0,1, myPos, arr);
    }

    private void swap(int first, int second, Vector2 myPos, GameCharacter[] arr){
        if (arr[first].getPlayerPos().dst(myPos) > arr[second].getPlayerPos().dst(myPos)){
            GameCharacter tmp = arr[first];
            arr[first] = arr[second];
            arr[second] = tmp;
        }
    }
}