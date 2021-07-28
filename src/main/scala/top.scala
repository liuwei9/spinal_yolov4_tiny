import spinal.core._
import spinal.lib.{master, slave}
class top  extends Component {
    val io = new Bundle {
        val image_Start = in Bool()
        val image_S_DATA = slave Stream Bits(8 bits)
//        val image_M_DATA = master Stream Bits(8 bits)
//        val image_Row_Num_After_Padding = out UInt (12 bits)
//        val image_Last = out (Reg(Bool())) init(False)
        val M_DATA = out Bits(24 bits)
        val M_Valid = out Bool()
        val M_Ready = in Bool()
        val M_rd_en = in Bool()
        val M_Addr = in UInt (12 bits)
        val StartRow = out Bool()
    }
    val image_padding = new padding(8,12,640).setDefinitionName("image_padding")
    image_padding.padding_fifo.setDefinitionName("image_padding_fifo")
    image_padding.padding_fifo.fifo.setDefinitionName("image_padding_fifo_sync")
    io.image_Start <> image_padding.io.Start
    io.image_S_DATA <> image_padding.io.S_DATA
//    io.image_M_DATA <> image_padding.io.M_DATA
//    io.image_Row_Num_After_Padding <> image_padding.io.Row_Num_After_Padding
//    io.image_Last <> image_padding.io.Last
    val image_four2three = new four2three(8,12,642).setDefinitionName("image_four2three")
    image_four2three.four2three_fifo.setDefinitionName("image_four2three_fifo")
    image_four2three.ram1.setDefinitionName("image_four2three_ram1")
    image_four2three.ram2.setDefinitionName("image_four2three_ram2")
    image_four2three.ram3.setDefinitionName("image_four2three_ram3")
    image_four2three.ram4.setDefinitionName("image_four2three_ram4")
    image_four2three.io.S_DATA<>image_padding.io.M_DATA
    image_four2three.io.Start <> io.image_Start
    image_four2three.io.Row_Num_After_Padding <> image_padding.io.Row_Num_After_Padding
    image_four2three.io.M_DATA <> io.M_DATA
    image_four2three.io.M_Valid <> io.M_Valid
    image_four2three.io.M_Ready <> io.M_Ready
    image_four2three.io.M_Addr <> io.M_Addr
    image_four2three.io.StartRow <> io.StartRow
    image_four2three.io.M_rd_en <> io.M_rd_en
}
object top{
    def main(args: Array[String]): Unit = {
        SpinalConfig(
            defaultConfigForClockDomains = ClockDomainConfig(clockEdge = RISING, resetKind = SYNC),
            oneFilePerComponent = true,
            headerWithDate = true,
            targetDirectory = "verilog"

        ).generateVerilog(new top)
    }
}
