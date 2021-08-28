import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

class padding(
                 S_DATA_WIDTH: Int,
                 M_DATA_WIDTH: Int,
                 ROW_COL_DATA_COUNT_WIDTH: Int,
                 CHANNEL_NUM_WIDTH: Int,
                 DATA_WIDTH: Int,
                 ZERO_NUM_WIDTH: Int,
                 MEMORY_DEPTH: Int
             ) extends Component {
    val io = new Bundle {
        val Start = in Bool()
        val S_DATA = slave Stream Bits(S_DATA_WIDTH bits)
        val M_DATA = master Stream Bits(M_DATA_WIDTH bits)
        val Row_Num_In_REG = in Bits (ROW_COL_DATA_COUNT_WIDTH bits)
        val Channel_In_Num_REG = in Bits (CHANNEL_NUM_WIDTH bits)
        val Padding_REG = in Bool()
        val Zero_Point_REG = in Bits (DATA_WIDTH bits)
        val Zero_Num_REG = in Bits (ZERO_NUM_WIDTH bits)
        val RowNum_After_Padding = out Bits (ROW_COL_DATA_COUNT_WIDTH bits)

    }
    noIoPrefix()

    val S_Count_Fifo = UInt(ROW_COL_DATA_COUNT_WIDTH bits)
    val fifo = new padding_fifo(S_DATA_WIDTH, S_DATA_WIDTH, MEMORY_DEPTH, ROW_COL_DATA_COUNT_WIDTH)
    fifo.io.wr_en <> io.S_DATA.valid
    fifo.io.data_in <> io.S_DATA.payload
    fifo.io.data_in_ready <> io.S_DATA.ready

    val EN_Row0 = Bool()
    val EN_Row1 = Bool()
    val EN_Col0 = Bool()
    val EN_Col1 = Bool()
    when(io.Padding_REG) {
        EN_Row0 := True
        EN_Row1 := True
        EN_Col0 := True
        EN_Col1 := True
    } otherwise {
        EN_Row0 := False
        EN_Row1 := False
        EN_Col0 := False
        EN_Col1 := False
    }
    val In_Size = RegNext(io.Row_Num_In_REG)
    val Out_Size = UInt(ROW_COL_DATA_COUNT_WIDTH bits)
    when(io.Padding_REG) {
        Out_Size := io.Row_Num_In_REG + 2 * io.Zero_Num_REG
    } otherwise {
        Out_Size := io.Row_Num_In_REG
    }
    val padding_fsm = new StateMachine {
        val IDLE = new State() with EntryPoint
        val INIT = new State()
        val M_Row_Wait = new State()
        val S_Row_Wait = new State()
        val M_Row_Read = new State()
        val Judge_Row = new State()
        val S_Left_Padding = new State()
        val M_Up_Down_Padding = new State()
        val M_Right_Padding = new State()


        val wait_cnt = UInt(6 bits) setAsReg()
        when(isActive(INIT)) {
            wait_cnt := wait_cnt + 1
        } otherwise {
            wait_cnt := 0
        }
        val init_en = Bool()
        when(wait_cnt === 5) {
            init_en := True
        } otherwise {
            init_en := False
        }

        val Cnt_Row = UInt(ROW_COL_DATA_COUNT_WIDTH bits) setAsReg()
        when(isActive(Judge_Row)){
            Cnt_Row := Cnt_Row + 1
        } elsewhen isActive(IDLE){
            Cnt_Row := 0
        } otherwise{
            Cnt_Row := Cnt_Row
        }
        val EN_Left_Padding = Bool()
        

        IDLE
            .whenIsActive {
                when(io.Start) {
                    goto(INIT)
                } otherwise goto(IDLE)
            }
        INIT
            .whenIsActive {
                when(init_en) {
                    goto(M_Row_Wait)
                } otherwise goto(INIT)
            }
        M_Row_Wait
            .whenIsActive {
                when(io.M_DATA.ready) {
                    when(EN_Row0) {
                        goto(S_Left_Padding)
                    } otherwise {
                        goto(S_Row_Wait)
                    }
                } otherwise {
                    goto(M_Row_Wait)
                }
            }
        S_Row_Wait
            .whenIsActive {
                when(fifo.io.data_out_valid) {
                    goto(M_Row_Read)
                } otherwise {
                    goto(S_Row_Wait)
                }
            }
        M_Row_Read
            .whenIsActive {

            }
        Judge_Row
            .whenIsActive {

            }
        S_Left_Padding
            .whenIsActive {

            }
        M_Up_Down_Padding
            .whenIsActive {

            }
        M_Right_Padding
            .whenIsActive {

            }

    }
}
