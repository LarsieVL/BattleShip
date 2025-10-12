class PlayerBoard extends Board {
    public PlayerBoard() {
        setSize(500, 500);
    }

    @Override
    void renderBoard() {
        paintGrid();
        paintShips();
    }
}