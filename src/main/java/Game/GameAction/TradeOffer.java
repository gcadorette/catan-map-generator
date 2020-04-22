package Game.GameAction;

import Board.Resources.Resource;

public class TradeOffer
{

    private Resource _givingResource;
    private Resource _demandingResource;

    public TradeOffer(Resource givingResource, Resource demandingResource)
    {
        _givingResource = givingResource;
        _demandingResource = demandingResource;
    }

    public Resource givingResource()
    {
        return _givingResource;
    }

    public Resource demandingResource()
    {
        return _demandingResource;
    }

    @Override
    public boolean equals(Object oth)
    {
        if(oth != null)
        {
            TradeOffer other = (TradeOffer)oth;
            return other.givingResource().equals(givingResource())
                   && other.demandingResource().equals(demandingResource());
        }
        return false;
    }

}
