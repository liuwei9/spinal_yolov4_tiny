// Generator : SpinalHDL v1.6.0    git head : 73c8d8e2b86b45646e9d0b2e729291f2b65e6be3
// Component : add_simd
// Git hash  : 2735a0a9a0d8255679cd14c554c6d83469e1ac84
// Date      : 20/08/2021, 19:05:30


module add_simd (
  input      [19:0]   A,
  input      [19:0]   B,
  output     [19:0]   P,
  input               clk,
  input               reset
);
  wire       [19:0]   add_72_P;

  add add_72 (
    .A        (A         ), //i
    .B        (B         ), //i
    .P        (add_72_P  ), //o
    .clk      (clk       ), //i
    .reset    (reset     )  //i
  );
  assign P = add_72_P;

endmodule
