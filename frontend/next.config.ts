import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Keep dev output separate from build output so validation builds do not
  // invalidate the running dev server's chunk manifest.
  distDir: process.env.NODE_ENV === "development" ? ".next-dev" : ".next",
  experimental: {
    useWasmBinary: true
  }
};

export default nextConfig;
