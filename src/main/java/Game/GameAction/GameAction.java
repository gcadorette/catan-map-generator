package Game.GameAction;

public abstract class GameAction
{
    private ActionType _actionType;

    public ActionType getActionType()
    {
        return _actionType;
    }

    public abstract void execute();
}
