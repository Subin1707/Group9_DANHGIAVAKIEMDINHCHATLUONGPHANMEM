module.exports = {
  testDir: './tests',
  use: {
    baseURL: 'http://localhost:8080',
    headless: false,
    screenshot: 'only-on-failure',
    video: 'retain-on-failure'
  }
};
