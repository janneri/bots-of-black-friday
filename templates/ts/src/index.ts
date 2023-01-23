export const main = () => {
  console.log("hello world");
};

if (require.main === module) {
  main();
}
